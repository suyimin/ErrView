package com.xdroid.library.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @description: 文件处理工具类
 * @author: pengd
 * @date: 2016-06-13 09:38
 */
public class FileUtils {

    private static final String TAG = "FileUtils";

    private FileUtils() {
        throw new IllegalStateException("FileUtils.class");
    }

    /**
     * @description: 获取SD卡路径
     * @return string
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    }

    /**
     * @description: 判断SD卡是否可用
     * @return boolean
     */
    public static boolean existFile(String filePath) {
        if(TextUtils.isEmpty(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return file.exists();
    }


    /**
     * @description: 判断SD卡是否可用
     * @return boolean
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        LogTool.d(TAG, "[method: isSDCardAvailable] sdcard is not available.");
        return false;
    }


    /**
     * @description: 获取路径所在存储区域剩余空间,单位byte，若存储位置为SD卡，且SD卡不可用，返回0
     * @param filePath
     * @return long
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            if (!isSDCardAvailable()) {
                return 0;
            }
            filePath = getSDCardPath();
        } else {// 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * @description: 创建文件
     * @param filePath
     * @return file
     */
    public static File createFile(String filePath) {
        return createFile(filePath, "755");
    }


    /**
     * @description: 创建文件，并修改读写权限
     * @param filePath
     * @param mode
     * @return file
     */
    private static File createFile(String filePath, String mode) {
        File desFile = null;
        try {
            String desDir = filePath.substring(0, filePath.lastIndexOf(File.separator));
            File dir = new File(desDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            chmodFile(dir.getAbsolutePath(), mode);
            desFile = new File(filePath);
            if (!desFile.exists()) {
                if(desFile.isDirectory()) {
                    desFile.createNewFile();
                }
            }
            chmodFile(desFile.getAbsolutePath(), mode);
        } catch (Exception e) {
            LogTool.e(TAG, "[method: createFile] error message: " + e.getMessage(), e);
        }
        return desFile;
    }

    /**
     * @description: 修改文件读写权限
     * @param fileAbsPath
     * @param mode
     */
    private static void chmodFile(String fileAbsPath, String mode) {
        String cmd = "chmod " + mode + " " + fileAbsPath;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            LogTool.e(TAG, "[method: chmodFile ] errorMsg: " + e.getMessage(), e);
        }
    }

    /**
     * @description: 拷贝文件，通过返回值判断是否拷贝成功
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @return
     */
    public static boolean copyFile(String sourcePath, String targetPath) {
        boolean isOK = false;
        //减少代码嵌套层级
        if (TextUtils.isEmpty(sourcePath) || TextUtils.isEmpty(targetPath)) {
            return isOK;
        }
        File sourcefile = new File(sourcePath);
        File targetFile = new File(targetPath);
        if (!sourcefile.exists()) {
            return isOK;
        }
        if (sourcefile.isDirectory()) {
            isOK = copyDirectory(sourcefile, targetFile);
        } else if (sourcefile.isFile()) {
            if (!targetFile.exists()) {
                FileUtils.createFile(targetPath);
            }
            FileOutputStream outputStream = null;
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(sourcefile);
                outputStream = new FileOutputStream(targetFile);
                byte[] bs = new byte[1024];
                int len;
                while ((len = inputStream.read(bs)) != -1) {
                    outputStream.write(bs, 0, len);
                }
                isOK = true;
            } catch (Exception e) {
                LogTool.e(TAG, "[method: copyFile] errorMsg: " + e.getMessage(), e);
                isOK = false;
            } finally {
                closeQuietly(inputStream);
                closeQuietly(outputStream);
            }
        }
        return isOK;
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
     * @description: 拷贝目录
     *
     * @param sourceFile
     * @param targetFile
     * @return
     */
    private static boolean copyDirectory(File sourceFile, File targetFile) {
        if (sourceFile == null || targetFile == null) {
            return false;
        }
        if (!sourceFile.exists()) {
            return false;
        }
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        // 获取目录下所有文件和文件夹的列表
        File[] files = sourceFile.listFiles();
        if (files == null || files.length < 1) {
            return false;
        }
        File file;
        StringBuilder buffer = new StringBuilder();
        boolean isSuccessful;
        // 遍历目录下的所有文件文件夹，分别处理
        for (int i = 0; i < files.length; i++) {
            file = files[i];
            buffer.setLength(0);
            buffer.append(targetFile.getAbsolutePath()).append(File.separator).append(file.getName());
            if (file.isFile()) {
                // 文件直接调用拷贝文件方法
                isSuccessful = copyFile(file.getAbsolutePath(), buffer.toString());
                if (!isSuccessful) {
                    return false;
                }
            } else if (file.isDirectory()) {
                // 目录再次调用拷贝目录方法
                copyDirectory(file, new File(buffer.toString()));
            }

        }
        return true;
    }

    /**
     * @description: 根据路径，获取文件
     * @param filePath
     * @return file
     */
    public static File getFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }

        File file = new File(filePath);
        if (file.exists()) {
            return file;
        }

        return null;
    }

    /**
     * @description: 删除文件
     * @param path
     * @return boolean
     */
    public static boolean deleteFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
            try {
                file.delete();
            } catch (Exception e) {
                LogTool.e(TAG, "[method: deleteFile ]: errorMsg: " + e.getMessage(), e);
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 删除文件夹及其子文件夹(文件)
     * @param path 文件夹目录
     * @return boolean
     */
    public static boolean deleteDir(String path) {
        FileUtils.chmodFile(path, "777");
        String cmd = "rm -rf " + path;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            LogTool.e(TAG, "[method: deleteDir]: errorMsg: " + e.getMessage(), e);
            return false;
        }
        return true;
    }

    /**
     * 获取HuaweiIT目录
     * @return
     */
    private static String getHuaweiITDir() {
        String huaweiITDir = "";
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            huaweiITDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator
                    + "Huawei" + File.separator + "HuaweiIT" + File.separator;
        }

        return huaweiITDir;
    }

    /**
     * DeCompress the ZIP to the path
     * @param zipFilePath  name of ZIP
     * @param outPath   path to be unZIP
     * @throws Exception
     */
    public static void unZip(String zipFilePath, String outPath) throws Exception {
        unZip(new FileInputStream(zipFilePath), outPath);
    }

    public static void unZip(InputStream inputStream, String outPath) throws Exception {
        ZipInputStream inZip = new ZipInputStream(inputStream);
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPath + File.separator + szName);
                folder.mkdirs();
            } else {
                doZip(outPath, inZip, szName);
            }
        }
        closeQuietly(inZip);
    }

    private static void doZip(String outPath, ZipInputStream inZip, String szName) {
        FileOutputStream out = null;
        try {
            File file = new File(outPath + File.separator + szName);
            if (!file.exists()) {
                File dir = file.getParentFile();
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                file.createNewFile();
            }
            // get the output stream of the file
            out = new FileOutputStream(file);
            int len;
            byte[] buffer = new byte[1024];
            // read (len) bytes into buffer
            while ((len = inZip.read(buffer)) != -1) {
                // write (len) byte from buffer at the position 0
                out.write(buffer, 0, len);
                out.flush();
            }
        } catch (IOException e) {
            LogTool.e(TAG, "[method: doZip]: errorMsg: " + e.getMessage(), e);
        } finally {
            closeQuietly(out);
        }
    }

    public static boolean writeObject(String dirPath, String outFile, Object object) {
        boolean result = false;
        ObjectOutputStream out = null;
        FileOutputStream outStream = null;

        try {
            File dir = new File(dirPath);
            if (!dir.exists()){
                dir.mkdirs();
            }
            File dataFile = new File(dir, outFile);
            if (!dataFile.exists()){
                dataFile.createNewFile();
            }
            outStream = new FileOutputStream(dataFile);
            out = new ObjectOutputStream(new BufferedOutputStream(outStream));
            out.writeObject(object);
            out.flush();
            result = true;
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            closeQuietly(outStream);
            closeQuietly(out);
        }
        return result;
    }

    public static Object readObject(String dirPath, String filePath) {
        Object object = null;
        ObjectInputStream in = null;
        FileInputStream inputStream = null;
        try {
            File dir = new File(dirPath);
            File f = new File(dir, filePath);
            if (!f.exists()) {
                return null;
            }
            inputStream = new FileInputStream(new File(dir, filePath));
            in = new ObjectInputStream(new BufferedInputStream(inputStream));
            object = in.readObject();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            closeQuietly(in);
            closeQuietly(inputStream);
        }
        return object;
    }
}