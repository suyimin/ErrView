package com.xdroid.library.widget;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xdroid.library.R;
import com.xdroid.library.utils.LogTool;
import com.xdroid.library.utils.AppContext;

import java.lang.reflect.Field;

public class XToast {
    private static final String TAG = "XToast";
    public static Toast makeText(Context context, CharSequence text, XPrompt XPrompt) {
        Toast toast = new Toast(context);
        View view = View.inflate(AppContext.getContext(), R.layout.toast_view_layout, null);
        ImageView toastImg = (ImageView) view.findViewById(R.id.toast_img);
        TextView textView = (TextView) view.findViewById(R.id.toast_hint);
        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.TOP, 0, 0);

        toast.setMargin(0, 0);
        if (XPrompt.WARNING == XPrompt) {
            toastImg.setVisibility(View.VISIBLE);
        } else {
            toastImg.setVisibility(View.GONE);
        }
        textView.setText(text);
        toast.setView(view);

        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            Object mTN = tnField.get(toast);

            /**设置动画*/
            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            params.windowAnimations = R.style.Wetoast_Animation;
        } catch (Exception e) {
            LogTool.e(TAG, e);
        }
        return toast;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = AppContext.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = AppContext.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
