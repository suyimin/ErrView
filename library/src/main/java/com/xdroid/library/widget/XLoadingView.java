package com.xdroid.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xdroid.library.R;
import com.xdroid.library.utils.AppContext;
import com.xdroid.library.utils.ResUtils;


/**
 * 提供公用的正在加载view（提示语统一由使用者传入，默认为正在加载）
 */
public class XLoadingView extends RelativeLayout {

    //LOG TAG
    private String TAG = XLoadingView.class.getSimpleName();

    // loading默认样式
    public static final int STYLE_NORMAL = 10;

    // 只有一个红圈的样式
    public static final int STYLE_RED_CIRCLE = 11;

    // 带有进度样式
    public static final int STYLE_PROGRESS = 12;

    // 百分比
    private TextView mProgress;

    private RelativeLayout mRlLogoContainer;

    //内部显示图片、动画的控件
    private ImageView mImageView;
    //内部显示提示语的控件
    private TextView mText;

    //此控件容器
    private LinearLayout mContainer;

    /**转圈动画**/
    private Animation rotateAnimation;

    public XLoadingView(Context context) {
        super(context);
        initWeLoadingView(context);
    }

    public XLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWeLoadingView(context);
    }

    public XLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWeLoadingView(context);
    }

    private void initWeLoadingView(Context context) {
        mContainer = (LinearLayout) View.inflate(AppContext.getContext(), R.layout.loading_view, null);
        mRlLogoContainer = (RelativeLayout) mContainer.findViewById(R.id.rl_loading_img_container);
        mText = (TextView) mContainer.findViewById(R.id.tv_loading_text);
        mImageView = (ImageView) mContainer.findViewById(R.id.iv_loading_img);
        rotateAnimation = AnimationUtils.loadAnimation(AppContext.getContext(), R.anim.dialog_spiner_processing);
        mImageView.setAnimation(rotateAnimation);
        addView(mContainer);
        mProgress = (TextView) mContainer.findViewById(R.id.progress);
        setLoadingStyle(STYLE_NORMAL);
    }

    /**
     * 设置view中间的log是否显示，该方法暂时只有RN在用
     *
     * @param isLogoVisible logo是否可见
     */
    public void setLogoVisible(boolean isLogoVisible) {
        if (isLogoVisible) {
            mRlLogoContainer.setBackgroundResource(R.mipmap.dialog_loading_progress_bg);
        } else {
            mRlLogoContainer.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mImageView.startAnimation(rotateAnimation);
    }

    @Override
    protected void onDetachedFromWindow() {
        rotateAnimation.cancel();
        super.onDetachedFromWindow();
    }

    /**
     * 设置加载样式
     *
     * @param style see STYLE_NORMAL,STYLE_RED_CIRCLE,STYLE_PROGRESS
     */
    public void setLoadingStyle(int style) {
        switch (style) {
            case STYLE_NORMAL:
                mImageView.clearAnimation();
                getmRlLogoContainer().setBackgroundResource(ResUtils.getMipmapId("dialog_loading_progress_bg"));
                getmImageView().setBackgroundResource(ResUtils.getMipmapId("dialog_loading_progress"));
                mProgress.setVisibility(View.GONE);
                findViewById(R.id.percent).setVisibility(View.GONE);
                mImageView.startAnimation(rotateAnimation);
                break;
            case STYLE_PROGRESS:
                mImageView.clearAnimation();

                getmRlLogoContainer().setBackgroundResource(ResUtils.getResId(AppContext.getContext(), "dialog_loading_progress_bg_white", "drawable"));
                getmImageView().setBackgroundResource(ResUtils.getMipmapId("dialog_loading_progress"));
                mProgress.setVisibility(View.VISIBLE);
                findViewById(R.id.percent).setVisibility(View.VISIBLE);
                setProgress(0);
                mImageView.startAnimation(rotateAnimation);
                break;
            case STYLE_RED_CIRCLE:
                mImageView.clearAnimation();
                getmRlLogoContainer().setBackgroundResource(0);
                getmImageView().setBackgroundResource(ResUtils.getMipmapId("dialog_loading_progress_small"));
                mProgress.setVisibility(View.GONE);
                findViewById(R.id.percent).setVisibility(View.GONE);
                mImageView.startAnimation(rotateAnimation);
                break;
        }
    }

    public void hidePercentView() {
        findViewById(R.id.percent).setVisibility(View.GONE);
    }

    public void setProgress(int progress) {
        setProgress(String.valueOf(progress));
    }

    public void setProgress(String progress) {
        mProgress.setText(progress);
    }

    public RelativeLayout getmRlLogoContainer(){
        return mRlLogoContainer;
    }

    public ImageView getmImageView(){
        return mImageView;
    }


    public void cancelAnimation(){
        rotateAnimation.cancel();
    }

    public void startAnimation(){
        mImageView.startAnimation(rotateAnimation);
    }

    public void setText(String text){
//        if(text != null){
//            mText.setVisibility(View.VISIBLE);
//        }
        mText.setText(text);
    }

    public void setTextColor(int color){
        mText.setTextColor(color);
    }

    public void setTextSize(float size){
        mText.setTextSize(size);
    }

    public void setTextViewVisible(int visibility){
        mText.setVisibility(visibility);
    }
    public int getTextViewVisibility(){
        return mText.getVisibility();
    }
}
