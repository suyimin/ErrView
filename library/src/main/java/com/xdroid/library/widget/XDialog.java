package com.xdroid.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdroid.library.R;
import com.xdroid.library.utils.DisplayUtils;
import com.xdroid.library.utils.AppContext;

public class XDialog extends Dialog implements DialogInterface {
    /**日志标签 */
    protected final String TAG = this.getClass().getSimpleName();
    private LinearLayout dialogLayout;
    /**标题栏Layout*/
    private LinearLayout titleLayout;
    /**Body区域layout*/
    private LinearLayout bodyLayout;
    /** 间隔线 */
    private View separatorLine;
    private View topSeparatorLine;
    /**底部栏Layout*/
    private LinearLayout bottomLayout;
    /**左按钮标识*/
    public static final int BUTTON_LEFT = 1;
    /**中按钮标识*/
    public static final int BUTTON_MIDDLE = 2;
    /**右按钮标识*/
    public static final int BUTTON_RIGHT = 3;

    private int bottomLayoutShow = View.VISIBLE;
    private TextView leftButton;
    private TextView rightButton;
    private TextView dialogTitle;
    private EditText dialogContent;
    private LinearLayout containerLayout;

    private boolean isRichContent = false;


    public XDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setupDialog();
    }

    protected XDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        setupDialog();
    }

    /**
     * 初始化对话框
     */
    private void setupDialog() {
        LayoutInflater inflater = (LayoutInflater) AppContext.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_new, null);
        setContentView(view);
        dialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
        setDialogWidth(280);
        titleLayout = (LinearLayout) findViewById(R.id.dialog_title_layout);
        bodyLayout = (LinearLayout) findViewById(R.id.dialog_content_layout);
        separatorLine = findViewById(R.id.dialog_bottom_separator_line);
        topSeparatorLine = findViewById(R.id.dialog_content_top_line);
        bottomLayout = (LinearLayout) findViewById(R.id.dialog_bottom_layout);
        leftButton = (TextView) findViewById(R.id.dialog_negative_button);
        rightButton = (TextView) findViewById(R.id.dialog_positive_button);
        dialogTitle = (TextView) findViewById(R.id.dialog_title);
        dialogContent = (EditText) findViewById(R.id.dialog_content);
        containerLayout = (LinearLayout) findViewById(R.id.container_layout);
    }

    /**
     * 设置左按钮
     *
     * @param text 按钮文本
     * @param listener 点击事件监听器
     */
    public XDialog setLeftButton(CharSequence text, final OnClickListener listener) {
        if (TextUtils.isEmpty(text)) {
            Log.e(TAG, "setLeftButton: text is null.");
        }
        if (leftButton.getVisibility() == View.GONE || leftButton.getVisibility() == View.INVISIBLE) {
            leftButton.setVisibility(View.VISIBLE);
        }
        leftButton.setText(text);
        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClick(XDialog.this, BUTTON_LEFT);
                }
            }
        });
        return this;
    }

    /**
     * 设置中按钮
     *
     * @param text 按钮文本
     * @param listener 点击事件监听器
     */
    public XDialog setMiddleButton(CharSequence text, final OnClickListener listener) {
        if (TextUtils.isEmpty(text) || bottomLayoutShow != View.VISIBLE) {
            Log.e(TAG, "setMiddleButton: " + (TextUtils.isEmpty(text) ? "button text is null." : "bottomLayoutShow is invisible."));
        }
        leftButton.setText(text);
        rightButton.setVisibility(View.GONE);
        leftButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClick(XDialog.this, BUTTON_MIDDLE);
                }
            }
        });
        return this;
    }

    /**
     * 设置右按钮
     *
     * @param text 按钮文本
     * @param listener 点击事件监听器
     */
    public XDialog setRightButton(CharSequence text, final OnClickListener listener) {
        if (TextUtils.isEmpty(text)) {
            Log.e(TAG, "setRightButton: button text is null.");
        }
        if (rightButton.getVisibility() == View.GONE || rightButton.getVisibility() == View.INVISIBLE) {
            rightButton.setVisibility(View.VISIBLE);
        }
        rightButton.setText(text);
        rightButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onClick(XDialog.this, BUTTON_RIGHT);
                }
            }
        });
        return this;
    }

    public TextView getLeftButton() {
        return leftButton;
    }

    public TextView getRightButton() {
        return rightButton;
    }

    public XDialog setMiddleButtonColor(int resId) {
        leftButton.setTextColor(resId);
        return this;
    }

    public XDialog setLeftButtonColor(int resId) {
        leftButton.setTextColor(resId);
        return this;
    }

    public XDialog setRightButtonColor(int resId) {
        rightButton.setTextColor(resId);
        return this;
    }

    /**
     * 设置标题栏自定义视图
     *
     * @param contentView 自定义视图
     * @param layoutParams 视图显示参数
     */
    public XDialog setTitleContentView(View contentView, ViewGroup.LayoutParams layoutParams) {
        if (null == contentView) {
            Log.e(TAG, "setTitleContentView: contentView is null.");
        }

        this.titleLayout.removeAllViews();
        if (null == layoutParams) {
            this.titleLayout.addView(contentView);
        } else {
            this.titleLayout.addView(contentView, layoutParams);
        }
        return this;
    }

    /**
     * 设置标题栏可见性
     *
     * @param visibility
     */
    public XDialog setTitleVisibility(int visibility) {
        this.titleLayout.setVisibility(visibility);
        return this;
    }

    /**
     * 设置标题栏文本
     *
     * @param title
     */
    public XDialog setTitleText(CharSequence title) {
        if (TextUtils.isEmpty(title)) {
            Log.e(TAG, "setTitleText: title is null.");
        }
        dialogTitle.setText(title);
        return this;
    }

    /**
     * 设置中间显示区域自定义视图
     *
     * @param contentView 自定义视图
     * @param layoutParams 视图显示参数
     */
    public XDialog setBodyContentView(View contentView, ViewGroup.LayoutParams layoutParams) {
        if (null == contentView) {
            Log.e(TAG, "setBodyContentView: contentView is null.");;
        }

        this.bodyLayout.removeAllViews();
        if (null == layoutParams) {
            this.bodyLayout.addView(contentView);
        } else {
            this.bodyLayout.addView(contentView, layoutParams);
        }
        return this;
    }

    /**
     * 设置中间显示区域可见性
     *
     * @param visibility
     */
    public XDialog setBodyVisibility(int visibility) {
        this.bodyLayout.setVisibility(visibility);
        return this;
    }

    /**
     * 设置中间显示区域文本内容
     *
     * @param text
     */
    public XDialog setBodyText(CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            Log.e(TAG, "setBodyText: text is null.");
        }
        if(isRichContent) {
            dialogContent.setTextSize(13);
            dialogContent.setText(Html.fromHtml(String.valueOf(text)));
        } else {
            dialogContent.setText(text);
        }
        return this;
    }

    /**
     * 设置BodyText的对齐方式。
     * @param gravity
     */
    public XDialog setBodyTextGravity(int gravity) {
        bodyLayout.setGravity(gravity);
        if(dialogContent != null){
            dialogContent.setGravity(gravity);
        }
        return this;
    }

    /**
     * 设置底部操作栏自定义视图
     *
     * @param contentView 自定义视图
     * @param layoutParams 视图显示参数
     */
    public XDialog setBottomContentView(View contentView, ViewGroup.LayoutParams layoutParams) {
        if (null == contentView) {
            Log.e(TAG, "setBottomContentView: contextView is null.");
        }
        this.bottomLayout.removeAllViews();
        if (null == layoutParams) {
            this.bottomLayout.addView(contentView);
        } else {
            this.bottomLayout.addView(contentView, layoutParams);
        }
        return this;
    }

    /**
     * 设置底部操作栏视图可见性
     *
     * @param visibility
     */
    public XDialog setBottomVisibility(int visibility) {
        bottomLayoutShow = visibility;
        bottomLayout.setVisibility(visibility);
        return this;
    }

    @Override
    public Window getWindow() {
        return super.getWindow();
    }

    /**
     * 设置底部操作栏视图可见性
     *
     * @param visibility
     */
    public void setSeparatorLineVisibility(int visibility) {
        separatorLine.setVisibility(visibility);
    }

    public LinearLayout getBodyLayout() {
        return bodyLayout;
    }

    public void setDialogWidth(int width) {
        FrameLayout.LayoutParams linearParams =  (FrameLayout.LayoutParams)dialogLayout.getLayoutParams();
        linearParams.width = DisplayUtils.dip2px(AppContext.getContext(), width);
        dialogLayout.setLayoutParams(linearParams);
    }

    protected void setDialogTypeWarning() {
        this.titleLayout.setBackgroundResource(R.drawable.title_bg);
        int titleColor = this.getContext().getResources().getColor(android.R.color.white);
        this.dialogTitle.setTextColor(titleColor);
        this.dialogTitle.setTextSize(18);
        TextPaint titlePaint = dialogTitle.getPaint();
        titlePaint.setFakeBoldText(true);
        setRightButtonColor(AppContext.getContext().getResources().getColor(R.color.button_text_xf04b3d));
        LinearLayout.LayoutParams linearParams =  (LinearLayout.LayoutParams)titleLayout.getLayoutParams();
        linearParams.topMargin = DisplayUtils.dip2px(AppContext.getContext(), 0);
        titleLayout.setLayoutParams(linearParams);
        titleLayout.setPadding(0, DisplayUtils.dip2px(AppContext.getContext(), 20), 0, DisplayUtils.dip2px(AppContext.getContext(), 20));

        bodyLayout.setPadding(DisplayUtils.dip2px(AppContext.getContext(), 16), DisplayUtils.dip2px(AppContext.getContext(), 20), DisplayUtils.dip2px(AppContext.getContext(), 16), DisplayUtils.dip2px(AppContext.getContext(), 20));

        setBodyContentForHtml();
    }

    protected void setDialogTypeContent() {
        setTitleVisibility(View.GONE);
        setTopSeparatorLine(View.GONE);
        containerLayout.setGravity(Gravity.CENTER);
        bodyLayout.setGravity(Gravity.CENTER);
        dialogContent.setGravity(Gravity.CENTER);
    }

    /**
     * 设置顶部间隔线显示
     *
     * @param showType
     */
    protected XDialog setTopSeparatorLine(int showType) {
        topSeparatorLine.setVisibility(showType);
        return this;
    }

    /**
     * 设置内容为html格式
     *
     */
    protected void setBodyContentForHtml() {
        isRichContent = true;
        String content= dialogContent.getText().toString();
        if(TextUtils.isEmpty(content)) {
            return;
        }
        containerLayout.setGravity(Gravity.LEFT);
        bodyLayout.setGravity(Gravity.LEFT);
        dialogContent.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
        dialogContent.setTextSize(13);
        dialogContent.setText(Html.fromHtml(content));
    }


}