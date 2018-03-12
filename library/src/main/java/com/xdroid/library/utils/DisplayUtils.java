package com.xdroid.library.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;

public class DisplayUtils {
	private static final String LOG_TAG = DisplayUtils.class.getSimpleName();

	/**
	 * 该方法的作用:dip转换为像素
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 该方法的作用:sp转换为px（文字大小单位）
	 */
	public static int sp2px(Context context, float spValue) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * scaledDensity + 0.5f);
	}

	/**
	 * 该方法的作用:px转换为sp（文字大小单位）
	 */
	public static int px2sp(Context context, float pxValue) {
		float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / scaledDensity + 0.5f);
	}

	/**
	 * px转换为dip
	 */
	public static int px2dip(Context context, float pxValue) {
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / density);
	}

	/**
	 * 该方法的作用:获取状态栏的高度
	 */
	public static int getStatusHeight(Activity activity) {
		int statusHeihgt;
		Rect localRect = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
		statusHeihgt = localRect.top;

		if (statusHeihgt == 0) {
			Class<?> localClass;
			try {
				localClass = Class.forName("com.android.internal.R$dimen");
				Object localObject = localClass.newInstance();
				int statusBar_Id =
						Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
				statusHeihgt = activity.getResources().getDimensionPixelSize(statusBar_Id);
			} catch (Exception e) {
				LogTool.e(LOG_TAG, "", e);
			}
		}
		return statusHeihgt;
	}

	/**
	 * 该方法的作用:获取屏幕分辨率
	 */
	public static int[] getDisplay(Activity context) {
		int[] size = new int[2];
		DisplayMetrics metrics = new DisplayMetrics();
		context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		size[0] = metrics.widthPixels;
		size[1] = metrics.heightPixels;
		return size;
	}
}
