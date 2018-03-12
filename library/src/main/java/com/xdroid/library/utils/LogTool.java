package com.xdroid.library.utils;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description: 日志打印工具类
 */
public class LogTool {
    private static final String TAG = "LogTool";
    /**
     * 日志写入文件开关(日志不输出到本地，修改为false)
     */
    private static Boolean LOG_WRITE_TO_FILE = false;

    /**
     * 日志写控制台开关(日志不输出到控制台, 默认为true)
     */
    private static Boolean LOG_PRINT_TO_CONSOLE = true;

    /**
     * debug级别 (生产环境，务必修改打印级别)
     */
    private static DebugLevel sDebugLevel = DebugLevel.VERBOSE;

    /**
     * 日志文件在sdcard中的路径
     */
    private static String LOG_PATH_SDCARD_DIR = "";

    /**
     * sd卡中日志文件的最多保存天数
     */
    private static int SDCARD_LOG_FILE_SAVE_DAYS = 5;

    /**
     * 本类输出的日志文件后缀名称(文件名格式为:yyyy-MM-dd_log.txt)
     */
    private static String LOG_FILE_PREFIX_NAME = ".log";

    private static String sTag = "WeLink";

    /**
     * 日志文件名称格式
     */
    private final static String FILE_NAME_PATTERN = "yyyyMMdd";

    /**
     * 日志时间戳格式
     */
    private final static String FILE_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSSZ";

    /**
     * 时间占位符
     */
    private static final String TIME_PLACEHOLDER = "TIME_PLACEHOLDER";

    /**
     * 临时存储日志的队列
     */
    public static ConcurrentLinkedQueue<String> tempLogQueue = new ConcurrentLinkedQueue<String>();

    /**
     * 日志线程是否停止标志
     */
    private static volatile boolean isLoopLogQuit = false;

    /**
     * 异步打印日志的线程
     */
    private static Thread mLogThread;

    /**
     * 日志打印互斥锁
     */
    private static Lock mLock = new ReentrantLock();

    /**
     * 日志打印互斥锁条件变量
     */
    private static Condition mCondition = mLock.newCondition();

    /**
     * 日志手动开关
     */
    private static boolean logSwitch = false;

    /**
     * 日志开关持久化到本地key
     */
    private static String KEY_LOG_SWITCH = "logSwitch";

    public static void start() {
        isLoopLogQuit = false;
        if (mLogThread == null) {
            mLogThread = new Thread(mLooperRunnable);
        }
        if (!mLogThread.isAlive()) {
            mLogThread.start();
        }
    }

    public static void stop() {
        isLoopLogQuit = true;
        if (mLogThread == null || !mLogThread.isAlive()) {
            return;
        }

        mLock.lock();
        mCondition.signal();
        mLock.unlock();
        tempLogQueue.clear();
        mLogThread = null;
    }

    protected static Runnable mLooperRunnable = new Runnable() {
        @Override
        public void run() {
            while (!isLoopLogQuit || !tempLogQueue.isEmpty()) {
                mLock.lock();
                String logStr = "";
                try {
                    // 没有日志到来则休眠
                    while (!isLoopLogQuit && tempLogQueue.isEmpty()) {
                        mCondition.await();
                    }
                    logStr = tempLogQueue.poll();
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage(), e);
                    Thread.currentThread().interrupt();
                } finally {
                    mLock.unlock();
                }
                    write(logStr);
            }
        }
    };

    /***
     * 打开写文件日志开关
     */
    public static void printFileOn() {
        if (!LOG_WRITE_TO_FILE) {
            LOG_WRITE_TO_FILE = true;
        }
    }

    /**
     * 关闭写文件日志开关
     */
    public static void printFileOff() {
        if (LOG_WRITE_TO_FILE) {
            LOG_WRITE_TO_FILE = false;
        }
    }

    /***
     * 打开控制台日志开关
     */
    public static void printConsoleOn() {
        if (!LOG_PRINT_TO_CONSOLE) {
            LOG_PRINT_TO_CONSOLE = true;
        }
    }

    /**
     * 关闭控制台日志开关
     */
    public static void printConsoleOff() {
        if (LOG_PRINT_TO_CONSOLE) {
            LOG_PRINT_TO_CONSOLE = false;
        }
    }

    public static String getTag() {
        return sTag;
    }

    public static void setTag(final String pTag) {
        sTag = pTag;
    }

    /**
     * 该方法的作用:获取打印级别
     */
    public static DebugLevel getDebugLevel() {
        return sDebugLevel;
    }

    /**
     * 该方法的作用:设置debug日期级别
     * @param pDebugLevel 级别顺序:NONE, ERROR, PROCESS, WARNING, INFO, DEBUG, VERBOSE （定义日志级别为PROGRESS的话,打印包括PROGRESS和ERROR的日志）
     */
    public static void setDebugLevel(DebugLevel pDebugLevel) {
        if (pDebugLevel == null) {
            throw new IllegalArgumentException("pDebugLevel must not be null!");
        }
        sDebugLevel = pDebugLevel;
    }

    public static void v(String pMessage) {
        v(sTag, pMessage, null);
    }

    public static void v(String pMessage, Throwable pThrowable) {
        v(sTag, pMessage, pThrowable);
    }

    public static void v(String pTag, String pMessage) {
        v(pTag, pMessage, null);
    }

    public static void v(String pTag, String pMessage, Throwable pThrowable) {
        v(null, pTag, pMessage, pThrowable);
    }

    /**
     * 该方法的作用:v级别og日志 参数: 返回值: 异常: 在什么情况下调用:
     */
    public static void v(String packageName, String pTag, String pMessage, Throwable pThrowable) {
        if (sDebugLevel.isSameOrLessThan(DebugLevel.VERBOSE)) {
            String logContent = pMessage;

            if (LOG_PRINT_TO_CONSOLE) {
                if (pThrowable == null) {
                    Log.v(pTag, pMessage);
                } else {
                    Log.v(pTag, pMessage, pThrowable);
                }
            }

            if (pThrowable != null) {
                logContent += ";  " + Log.getStackTraceString(pThrowable);
            }

            writeLogToBuffer(packageName, "V", pTag, logContent);
        }
    }

    public static void d(String pMessage) {
        d(sTag, pMessage, null);
    }

    public static void d(String pMessage, Throwable pThrowable) {
        d(sTag, pMessage, pThrowable);
    }

    public static void d(String pTag, String pMessage) {
        d(pTag, pMessage, null);
    }

    public static void d(String pTag, String pMessage, Throwable pThrowable) {
        d(null, pTag, pMessage, pThrowable);
    }

    /**
     * 该方法的作用:d级别og日志 参数: 返回值: 异常: 在什么情况下调用:
     */
    public static void d(String packageName, String pTag, String pMessage, Throwable pThrowable) {
        if (sDebugLevel.isSameOrLessThan(DebugLevel.DEBUG)) {
            String logContent = pMessage;

            if (LOG_PRINT_TO_CONSOLE) {
                if (pThrowable == null) {
                    Log.d(pTag, pMessage);
                } else {
                    Log.d(pTag, pMessage, pThrowable);
                }
            }

            if (pThrowable != null) {
                logContent += ";  " + Log.getStackTraceString(pThrowable);
            }
            writeLogToBuffer(packageName, "D", pTag, logContent);
        }
    }

    public static void i(String pMessage) {
        i(sTag, pMessage, null);
    }

    public static void i(String pMessage, Throwable pThrowable) {
        i(sTag, pMessage, pThrowable);
    }

    public static void i(String pTag, String pMessage) {
        i(pTag, pMessage, null);
    }

    public static void i(String pTag, String pMessage, Throwable pThrowable) {
        i(null, pTag, pMessage, pThrowable);
    }

    /**
     * 该方法的作用:i级别og日志 参数: 返回值: 异常: 在什么情况下调用:
     */
    public static void i(String packageName, String pTag, String pMessage, Throwable pThrowable) {
        if (sDebugLevel.isSameOrLessThan(DebugLevel.INFO)) {
            String logContent = pMessage;

            if (LOG_PRINT_TO_CONSOLE) {
                if (pThrowable == null) {
                    Log.i(pTag, pMessage);
                } else {
                    Log.i(pTag, pMessage, pThrowable);
                }
            }

            if (pThrowable != null) {
                logContent += ";  " + Log.getStackTraceString(pThrowable);
            }
            writeLogToBuffer(packageName, "I", pTag, logContent);
        }
    }

    public static void w(String pMessage) {
        w(sTag, pMessage, null);
    }

    public static void w(Throwable pThrowable) {
        w(sTag, "", pThrowable);
    }

    public static void w(String pMessage, Throwable pThrowable) {
        w(sTag, pMessage, pThrowable);
    }

    public static void w(String pTag, String pMessage) {
        w(pTag, pMessage, null);
    }

    public static void w(String pTag, String pMessage, Throwable pThrowable) {
        w(null, pTag, pMessage, pThrowable);
    }

    /**
     * 该方法的作用:w级别og日志 参数: 返回值: 异常: 在什么情况下调用:
     */
    public static void w(String packageName, String pTag, String pMessage, Throwable pThrowable) {
        if (sDebugLevel.isSameOrLessThan(DebugLevel.WARNING)) {
            String logContent = pMessage;

            if (LOG_PRINT_TO_CONSOLE) {
                if (pThrowable == null) {
                    Log.w(pTag, pMessage);
                } else {
                    Log.w(pTag, pMessage, pThrowable);
                }
            }

            if (pThrowable != null) {
                logContent += ";  " + Log.getStackTraceString(pThrowable);
            }
            writeLogToBuffer(packageName, "W", pTag, logContent);
        }
    }

    public static void e(String pMessage) {
        e(sTag, pMessage, null);
    }

    public static void e(Throwable pThrowable) {
        e(sTag, "", pThrowable);
    }

    public static void e(String pMessage, Throwable pThrowable) {
        e(sTag, pMessage, pThrowable);
    }

    public static void e(final String pTag, final String pMessage) {
        e(pTag, pMessage, null);
    }

    public static void e(String pTag, String pMessage, Throwable pThrowable) {
        e(null, pTag, pMessage, pThrowable);
    }

    /**
     * 该方法的作用:e级别og日志 参数: 返回值: 异常: 在什么情况下调用:
     */
    public static void e(String packageName, String pTag, String pMessage, Throwable pThrowable) {
        if (sDebugLevel.isSameOrLessThan(DebugLevel.ERROR)) {
            String logContent = pMessage;

            if (LOG_PRINT_TO_CONSOLE) {
                if (pThrowable == null) {
                    Log.e(pTag, pMessage);
                } else {
                    Log.e(pTag, pMessage, pThrowable);
                }
            }

            if (pThrowable != null) {
                logContent += ";  " + Log.getStackTraceString(pThrowable);
            }
            writeLogToBuffer(packageName, "E", pTag, logContent);
        }
    }

    public static void p(String pMessage) {
        p(sTag, pMessage, null);
    }

    public static void p(Throwable pThrowable) {
        p(sTag, "", pThrowable);
    }

    public static void p(String pMessage, Throwable pThrowable) {
        p(sTag, pMessage, pThrowable);
    }

    public static void p(String pTag, String pMessage) {
        p(pTag, pMessage, null);
    }

    public static void p(String pTag, String pMessage, Throwable pThrowable) {
        p(null, pTag, pMessage, pThrowable);
    }

    /**
     * 该方法的作用:p级别log,用于打印业务流程中的重要过程日志
     */
    public static void p(String packageName, String pTag, String pMessage, Throwable pThrowable) {
        if (sDebugLevel.isSameOrLessThan(DebugLevel.PROCESS)) {
            String logContent = pMessage;

            if (LOG_PRINT_TO_CONSOLE) {
                if (pThrowable == null) {
                    Log.i(pTag, pMessage);
                } else {
                    Log.i(pTag, pMessage, pThrowable);
                }
            }

            if (pThrowable != null) {
                logContent += ";  " + Log.getStackTraceString(pThrowable);
            }
            writeLogToBuffer(packageName, "P", pTag, logContent);
        }
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     **/
    private static void writeLogToBuffer(String packageName, String logLevel, String tag, String text) {// 新建或打开日志文件
        if (mLogThread == null || !mLogThread.isAlive() || isLoopLogQuit) {
            start();
        }

        // 设置日志文件路径
        if (TextUtils.isEmpty(getLogPathSdcardDir())) {
            return;
        }

        if (!TextUtils.isEmpty(tag) && tag.length() < 30) {
            for (int i = tag.length(); i < 30; i++) {
                tag += " ";
            }
        }

        String needWriteMessage = TextUtils.isEmpty(packageName) ?
                (logLevel + "    [" + TIME_PLACEHOLDER + "]    [" + tag + "]    " + text) :
                (logLevel + "    [" + TIME_PLACEHOLDER + "]    [" + packageName + "]    [" + tag + "]    " + text);

        mLock.lock();
        //添加日志到临时队列
        tempLogQueue.add(needWriteMessage);
        //通知线程循环，有日志要打印，请立即处理
        mCondition.signal();
        mLock.unlock();
    }

    /**
     * 获取日志目录
     *
     * @return LOG_PATH_SDCARD_DIR
     */
    public static String getLogPathSdcardDir() {
        if (LOG_PATH_SDCARD_DIR.equals("")) {
            boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
            if (sdCardExist) {
                LOG_PATH_SDCARD_DIR =
                        Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                                + "ErrView";
            }
        }
        return LOG_PATH_SDCARD_DIR;
    }

    public static void write(String log) {
        if (TextUtils.isEmpty(log)) {
            return;
        }

        Date nowtime = new Date();
        log = log.replace(TIME_PLACEHOLDER, getFormativeDateStr(nowtime, 2));

        FileWriter filerWriter = null;
        BufferedWriter bufWriter = null;

        try {
            String dateStr = getFormativeDateStr(nowtime, 1);
            String fileName = sTag + "_" + dateStr + LOG_FILE_PREFIX_NAME;
            File file = new File(LOG_PATH_SDCARD_DIR + File.separator + dateStr, fileName);
            File dir = new File(file.getParent());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
                deleteOutDateLog();// 创建新w3日志文件时，删除过期的日志
            }

            filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(log);
            bufWriter.newLine();
            bufWriter.flush();
            filerWriter.flush();
        } catch (Exception e) {
            Log.e("LogTools", e.getMessage(), e);
            printFileOff();
        } finally {
            closeQuietly(bufWriter);
            closeQuietly(filerWriter);
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if(closeable != null) {
                closeable.close();
            }
        } catch (IOException var2) {

        }

    }

    /**
     * 获取日志保存路径
     *
     * @return path
     */
    public static String getLogDirPath() {
        String path = "";
        Date nowtime = new Date();
        String dateStr = getFormativeDateStr(nowtime, 1);
        String logPath = getLogPathSdcardDir();
        if (!TextUtils.isEmpty(logPath)) {
            path = logPath + File.separator + dateStr;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        return path;
    }

    /**
     * 该方法的作用:获取格式化的日期字符串
     */
    private static String getFormativeDateStr(Date nowdate, int type) {
        String str = "";
        try {
            if (type == 1) {
                /** 日志文件格式 */
                final SimpleDateFormat logfileSf = new SimpleDateFormat(FILE_NAME_PATTERN);
                str = logfileSf.format(nowdate);
            } else {
                /** 日志时间戳输出格式 */
                final SimpleDateFormat LogSdf = new SimpleDateFormat(FILE_TIMESTAMP_PATTERN);
                str = LogSdf.format(nowdate);
            }
        } catch (Exception e) {
            Log.e("LogTools", e.getMessage(), e);
        }
        return str;
    }

    /**
     * 该方法的作用:删除过时的日志文件
     */
    private static void deleteOutDateLog() {
        File dir = new File(LOG_PATH_SDCARD_DIR);
        if (!dir.exists()) {
            return;
        }
        for (File file : dir.listFiles()) {
            Date beforeDate = getDateBefore();
            if (file.isDirectory()) {
                Date logCalendar = getDateByStr(file.getName());
                if (logCalendar.before(beforeDate)) {
                    FileUtils.deleteDir(file.getAbsolutePath());
                }
            }
            if (file.getName().endsWith(LOG_FILE_PREFIX_NAME)) {
                String logDateStr = file.getName().replace(LOG_FILE_PREFIX_NAME, "");
                Date logCalendar = getDateByStr(logDateStr);

                if (logCalendar.before(beforeDate)) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 得到现在时间前的几天日期，用来得到需要删除的日志文件名
     */
    private static Date getDateBefore() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.DATE, now.get(Calendar.DATE) - SDCARD_LOG_FILE_SAVE_DAYS);
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        return now.getTime();
    }

    /**
     * 该方法的作用:字符串转换成日期
     */
    private static synchronized Date getDateByStr(String dateStr) {
        Date date = new Date();
        try {
            final SimpleDateFormat logfileSf = new SimpleDateFormat(FILE_NAME_PATTERN);
            date = logfileSf.parse(dateStr);
        } catch (ParseException e) {
            Log.e("LogTools", e.getMessage(), e);
        }
        return date;
    }

    /**
     * 该方法的作用:删除所有日志文件
     */
    public static void deleteAllLogFile() {
        File dir = new File(LOG_PATH_SDCARD_DIR);
        if (dir.exists()) {
            deleteAllFiles(dir.getAbsolutePath());
        }
    }

    /**
     * 该方法的作用:删除目录下的所有文件
     */
    private static void deleteAllFiles(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        deleteFile(file);
    }

    /**
     * 该方法的作用: 删除文件或目录
     */
    public static void deleteFile(File file) {
        if (file.exists() && file.isDirectory()) {
            if (file.listFiles().length == 0) {
                file.delete();
                return;
            } else {
                File delFile[] = file.listFiles();
                int childFileCount = file.listFiles().length;
                for (int i = 0; i < childFileCount; i++) {
                    if (delFile[i].isDirectory()) {
                        // 递归调用del方法并取得子目录路径
                        deleteFile(delFile[i]);
                    }

                    delFile[i].delete();
                }
            }

            file.delete();
        } else if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    /**
     * 计算日志字节长度
     *
     * @param strLog
     * @return
     */
    private static int calcLogSize(String strLog) {
        byte[] logByte = strLog.getBytes();
        return logByte.length;
    }

    /**
     * 该类作用及功能说明:该类作用及功能说明---log级别枚举类
     */
    public static enum DebugLevel implements Comparable<DebugLevel> {
        NONE, ERROR, PROCESS, WARNING, INFO, DEBUG, VERBOSE;

        public static DebugLevel ALL = DebugLevel.VERBOSE;

        public boolean isSameOrLessThan(final DebugLevel pDebugLevel) {
            return this.compareTo(pDebugLevel) >= 0;
        }
    }

    /**
     * 如果是华为手机，获取emui号
     *
     * @return
     */
    private static String getEmui() {
        String manufacturer = Build.MANUFACTURER;
        String emui = "";
        if (!TextUtils.isEmpty(manufacturer) && "huawei".equalsIgnoreCase(manufacturer)) {
            try {
                Class<?> classType = Class.forName("android.os.SystemProperties");
                Method getMethod = classType.getDeclaredMethod("get", new Class<?>[]{String.class});
                emui = (String) getMethod.invoke(classType, new Object[]{"ro.build.version.emui"});
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        if (!TextUtils.isEmpty(emui)) {
            emui = ", EMUI=" + emui;
        }

        return emui;
    }
}
