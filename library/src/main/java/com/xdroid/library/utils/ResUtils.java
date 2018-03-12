package com.xdroid.library.utils;

import android.content.Context;

/**
 * 提供根据名称查找资源的方法,方便在插件中获取基座中的资源。
 */
public class ResUtils {
    private static final String RES_ID = "id";
    private static final String RES_STRING = "string";
    private static final String RES_DRABLE = "drawable";
    private static final String RES_MIPMAP = "mipmap";
    private static final String RES_LAYOUT = "layout";
    private static final String RES_STYLE = "style";
    private static final String RES_COLOR = "color";
    private static final String RES_DIMEN = "dimen";
    private static final String RES_ANIM = "anim";
    private static final String RES_MENU = "menu";
    private static Context context = AppContext.getContext();

    /**
     * 获取资源文件的id
     *
     * @param resName
     * @return
     */
    public static int getId(String resName) {
        return getResId(context, resName, RES_ID);
    }

    /**
     * 获取资源文件string的id
     *
     * @param resName
     * @return
     */
    public static int getStringId(String resName) {
        return getResId(context, resName, RES_STRING);
    }

    /**
     * 获取资源文件mipmap的id
     *
     * @param resName
     * @return
     */
    public static int getMipmapId(String resName) {
        return getResId(context, resName, RES_MIPMAP);
    }

    /**
     * 获取资源文件drable的id
     *
     * @param resName
     * @return
     */
    public static int getDrableId(String resName) {
        return getResId(context, resName, RES_DRABLE);
    }

    /**
     * 获取资源文件layout的id
     *
     * @param resName
     * @return
     */
    public static int getLayoutId(String resName) {
        return getResId(context, resName, RES_LAYOUT);
    }

    /**
     * 获取资源文件style的id
     *
     * @param resName
     * @return
     */
    public static int getStyleId(String resName) {
        return getResId(context, resName, RES_STYLE);
    }

    /**
     * 获取资源文件color的id
     *
     * @param resName
     * @return
     */
    public static int getColorId(String resName) {
        return getResId(context, resName, RES_COLOR);
    }

    /**
     * 获取资源文件dimen的id
     *
     * @param resName
     * @return
     */
    public static int getDimenId(String resName) {
        return getResId(context, resName, RES_DIMEN);
    }

    /**
     * 获取资源文件ainm的id
     *
     * @param resName
     * @return
     */
    public static int getAnimId(String resName) {
        return getResId(context, resName, RES_ANIM);
    }

    /**
     * 获取资源文件menu的id
     */
    public static int getMenuId(String resName) {
        return getResId(context, resName, RES_MENU);
    }

    /**
     * 获取资源文件ID
     *
     * @param context
     * @param resName
     * @param defType
     * @return
     */
    public static int getResId(Context context, String resName, String defType) {
        return context.getResources().getIdentifier(resName, defType, context.getPackageName());
    }
}
