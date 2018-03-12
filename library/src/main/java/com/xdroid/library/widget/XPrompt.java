package com.xdroid.library.widget;

import com.xdroid.library.R;

/**
 * 提示警告类
 */
public enum XPrompt {

    NORMAL(0, R.color.prompt_normal_bg),
    WARNING(R.mipmap.bounced_icon_warning, R.color.prompt_warning_bg);

    private int resIcon;
    private int backgroundColor;

    XPrompt(int resIcon, int backgroundColor) {
        this.resIcon = resIcon;
        this.backgroundColor = backgroundColor;
    }

    public int getResIcon() {
        return resIcon;
    }

    public void setResIcon(int resIcon) {
        this.resIcon = resIcon;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
