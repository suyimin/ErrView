package com.xdroid.library.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xdroid.library.R;
import com.xdroid.library.utils.DisplayUtils;
import com.xdroid.library.utils.ResUtils;
import com.xdroid.library.utils.AppContext;


/**
 * 提供公用的EmptyView（提示语统一由使用者传入）
 * 1.统一无数据提示页面
 * 2.统一的加载失败的页面
 */
public class XEmptyView extends LinearLayout {
    //LOG TAG
    private String TAG = XEmptyView.class.getSimpleName();
    //上下文（ApplicationContext）
    private Context mContext;

    //显示静态图片
    private ImageView mStatusImage;

    //内部显示主提示语的控件
    private TextView mMainText;

    //内部显示提示语的控件
    private TextView mText;

    //内部显示附加提示语的控件
    private TextView mExtraText;

    //附加内部容器
    private LinearLayout mExtraContainer;

    /**空数据*/
    public final static int VIEW_TYPE_EMPTY = 0;
    /**服务忙*/
    public final static int VIEW_TYPE_SYSTEM_BUSY = 1;
    /**无权限*/
    public final static int VIEW_TYPE_NO_RIGHT = 2;
    /**错误404*/
    public final static int VIEW_TYPE_ERROR_404 = 3;
    /**无网络连接*/
    public final static int VIEW_TYPE_NO_NETWORK = 4;
    /**模块正在建设中*/
    public final static int VIEW_TYPE_IS_BUILDING = 5;
    /**手机端无权限，只能在PC端查看*/
    public final static int VIEW_TYPE_IS_NO_RIGHT_ON_MOBILE = 6;


    int idx = 0;

    private OnRetryListener mRetryListener;

    public XEmptyView(Context context) {
        super(context);
        initWeEmptyView(context);
    }

    public XEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWeEmptyView(context);
    }

    public XEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWeEmptyView(context);
    }

    /**
     * 设置view的类型，该方法暂时只有RN在用
     */
    public void setType(int type) {
        CharSequence mainText = mMainText.getText();
        CharSequence extraText = mExtraText.getText();
        showView(type, mainText == null ? "" : mainText.toString(), extraText == null ? "" : extraText.toString());
    }

    /**
     * 设置主提示语，该方法暂时只有RN在用
     *
     * @param mainText 提示语
     */
    public void setMainText(String mainText) {
        mMainText.setText(mainText);
        mMainText.setVisibility(VISIBLE);
    }

    /**
     * 设置附加提示语，该方法暂时只有RN在用
     *
     * @param extraText 附加提示语
     */
    public void setExtraText(String extraText) {
        mExtraText.setText(extraText);
        mExtraText.setVisibility(VISIBLE);
    }

    /**
     * 初始化EmptyView
     *
     * @param context 上下文
     */
    private void initWeEmptyView(Context context) {
        //设置布局方向
        setOrientation(LinearLayout.VERTICAL);
        //设置布局居中
        setGravity(Gravity.CENTER);

        setBackgroundResource(0);

        mContext = context.getApplicationContext();

        //设置默认图片长宽均为156dp
        int size = DisplayUtils.dip2px(context,156);
        LayoutParams imageLayoutParams = new LayoutParams(size, size);
        imageLayoutParams.gravity = Gravity.CENTER;
        imageLayoutParams.bottomMargin = DisplayUtils.dip2px(context, 20);

        /**
         * 初始化静态图图片组件
         */
        mStatusImage = new ImageView(context);
        mStatusImage.setImageDrawable(AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty));
        mStatusImage.setLayoutParams(imageLayoutParams);


        LayoutParams mMainTextLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mMainText = new TextView(context);
        mMainText.setGravity(Gravity.CENTER_HORIZONTAL);
        mMainText.setTextSize(16);
        mMainText.setTextColor(AppContext.getContext().getResources().getColor(ResUtils.getColorId("dialog_text_x333333")));

        LayoutParams mTextLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mText = new TextView(context);
        mText.setGravity(Gravity.CENTER_HORIZONTAL);
        mText.setTextSize(14);
        mText.setTextColor(AppContext.getContext().getResources().getColor(ResUtils.getColorId("loading_view_text_x999999")));

        LayoutParams mExtraTextLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mExtraText = new TextView(context);
        mExtraText.setGravity(Gravity.CENTER_HORIZONTAL);
        mExtraText.setTextSize(12);
        mExtraText.setTextColor(AppContext.getContext().getResources().getColor(ResUtils.getColorId("loading_view_text_x999999")));

        LayoutParams mExtraContainerLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, DisplayUtils.dip2px(getContext(), 84));
        mExtraContainer = new LinearLayout(context);
        mExtraContainer.setOrientation(VERTICAL);

        addView(mStatusImage);
        addView(mMainText, mMainTextLayoutParams);
        addView(mExtraText, mExtraTextLayoutParams);
        addView(mText, mTextLayoutParams);
        addView(mExtraContainer, mExtraContainerLayoutParams);
    }

    /**
     * 显示空视图（背景海陆空随机切换）
     *
     * @param resId 提示语的资源ID
     */
    @Deprecated
    public void showEmptyView(int resId) {
        showEmptyView(mContext.getResources().getString(resId));
    }

    /**
     * 显示空视图（背景海陆空随机切换）
     *
     * @param text 提示语
     */
    @Deprecated
    public void showEmptyView(String text) {
        mStatusImage.setImageDrawable(AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty));
        mText.setText(text);
        mText.setClickable(false);
    }

    /**
     * 显示统一的错误页面
     *
     * @param resId 提示语的资源ID
     */
    @Deprecated
    public void showErrorView(int resId) {
        showErrorView(mContext.getResources().getString(resId));
    }

    /**
     * 显示统一的错误页面
     *
     * @param text 提示语
     */
    @Deprecated
    public void showErrorView(String text) {
        mStatusImage.setImageDrawable(AppContext.getContext().getResources().getDrawable(R.mipmap.x_load_failed));
        mText.setClickable(true);
        mText.setText(text);
        if (null != mRetryListener) {
            mText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRetryListener.onRetry();
                }
            });
        }
    }


    /**
     * 显示要展示的内容
     * @param text 文字描述
     */
    public void showView(CharSequence text){
        Drawable drawable = AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty_no_right);
        mMainText.setVisibility(GONE);
        mExtraText.setVisibility(GONE);
        mText.setVisibility(VISIBLE);
        mText.setText(text);
        mText.setMovementMethod(LinkMovementMethod.getInstance());
        mStatusImage.setImageDrawable(drawable);
    }

    /**
     * 显示要展示的内容
     * @param type 类型
     * @param text 主文字描述
     * @param extText 附文字描述
     */
    public void showView(int type, String text, String extText){
        Drawable drawable = AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty);
        String mainText = "";
        String extraText = "";
        String normalText = "";
        switch (type){
            case VIEW_TYPE_SYSTEM_BUSY:
                drawable = AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty_system_busy);
                mainText = AppContext.getContext().getResources().getString(R.string.widget_empty_system_busy);
                extraText = AppContext.getContext().getResources().getString(R.string.widget_empty_click_text);
                mMainText.setVisibility(VISIBLE);
                mExtraText.setVisibility(VISIBLE);
                mText.setVisibility(GONE);
                if(text != null){
                    mainText = text;
                }

                if(extText != null){
                    extraText = extText;
                }
                break;
            case VIEW_TYPE_NO_RIGHT:
                drawable = AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty_no_right);
                normalText = AppContext.getContext().getResources().getString(R.string.widget_empty_no_right);
                mMainText.setVisibility(GONE);
                mExtraText.setVisibility(GONE);
                mText.setVisibility(VISIBLE);
                if(text != null){
                    normalText = text;
                }
                break;
            case VIEW_TYPE_ERROR_404:
                drawable = AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty_error_404);
                normalText = AppContext.getContext().getResources().getString(R.string.widget_empty_error_404);
                mMainText.setVisibility(GONE);
                mExtraText.setVisibility(GONE);
                mText.setVisibility(VISIBLE);
                if(text != null){
                    normalText = text;
                }
                break;
            case VIEW_TYPE_NO_NETWORK:
                drawable = AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty_no_network);
                mainText = AppContext.getContext().getResources().getString(R.string.widget_empty_no_network);
                extraText = AppContext.getContext().getResources().getString(R.string.widget_empty_click_text);
                mMainText.setVisibility(VISIBLE);
                mExtraText.setVisibility(VISIBLE);
                mText.setVisibility(GONE);

                if(text != null){
                    mainText = text;
                }

                if(extText != null){
                    extraText = extText;
                }

                break;
            case VIEW_TYPE_IS_BUILDING:
                drawable = AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty_is_building);
                normalText = AppContext.getContext().getResources().getString(R.string.widget_empty_is_building);
                mMainText.setVisibility(GONE);
                mExtraText.setVisibility(GONE);
                mText.setVisibility(VISIBLE);
                if(text != null){
                    normalText = text;
                }
                break;
            case VIEW_TYPE_IS_NO_RIGHT_ON_MOBILE:
                drawable = AppContext.getContext().getResources().getDrawable(R.mipmap.x_empty_is_no_right_on_mobile);
                normalText = AppContext.getContext().getResources().getString(R.string.widget_empty_is_no_right_on_mobile);
                mMainText.setVisibility(GONE);
                mExtraText.setVisibility(GONE);
                mText.setVisibility(VISIBLE);
                if(text != null){
                    normalText = text;
                }
                break;
            default:
                mMainText.setVisibility(GONE);
                mExtraText.setVisibility(GONE);
                mText.setVisibility(GONE);
                if (!TextUtils.isEmpty(text)) {
                    normalText = text;
                    mText.setVisibility(VISIBLE);
                }
                if (!TextUtils.isEmpty(extText)) {
                    mainText = extText;
                    mMainText.setVisibility(VISIBLE);
                }
                break;
        }
        mMainText.setText(mainText);
        mExtraText.setText(extraText);
        mText.setText(normalText);
        mStatusImage.setImageDrawable(drawable);

        if (null != mRetryListener) {
            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mRetryListener.onRetry();
                }
            });
        }

    }




    public void setOnRetryListener(OnRetryListener mOnRetryListener) {
        this.mRetryListener = mOnRetryListener;
    }

    public interface OnRetryListener {
        void onRetry();
    }

    public void onDestroy() {
        mStatusImage = null;
        mText = null;
    }

    public LinearLayout getmExtraContainer() {
        return mExtraContainer;
    }

    public void setmExtraContainer(LinearLayout mExtraContainer) {
        this.mExtraContainer = mExtraContainer;
    }
}
