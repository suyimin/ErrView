package com.xdroid.library.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.xdroid.library.R;
import com.xdroid.library.utils.DisplayUtils;
import com.xdroid.library.utils.AppContext;

public class XImageButton extends LinearLayout {
	private final int TEXT_VIEW = 1;
	private final int IMAGE_VIEW = 2;
	private Context mContext;

	private ImageView imageView;
	private TextView textView;

	/** 徽章视图 */
	private XBadgeView XBadgeView;

	private float textSize = 16f;
	private int textColor =  0xFFF0F0F0;

	public XImageButton(Context context) {
		this(context, null);
	}

	public XImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}

	private void initView() {
		this.setFocusable(true);
		this.setClickable(true);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		this.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
		this.setLayoutParams(params);
	}

	/**
	 * 该方法的作用:获取内容视图
	 */
	private View getContentView(int type) {
		if (type == IMAGE_VIEW) {
			if (imageView == null) {
				imageView = new ImageView(mContext);
				imageView.setClickable(false);
				imageView.setFocusable(false);
				LayoutParams params =
						new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER;
				if (this.getChildCount() > 0) {
					this.removeAllViews();
					textView = null;
				}
				this.addView(imageView, params);
			}
			return imageView;
		} else if (type == TEXT_VIEW) {
			if (textView == null) {
				textView = new TextView(mContext);
				textView.setClickable(false);
				textView.setFocusable(false);
				textView.setSingleLine();
				textView.setTextSize(16);
				textView.setEllipsize(TruncateAt.END);
				ColorStateList stateList =	AppContext.getContext().getResources().getColorStateList(R.color.nav_bar_btn_text_selector);
				textView.setTextColor(stateList);
				LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.leftMargin = DisplayUtils.dip2px(getContext(), 2);
				params.rightMargin = DisplayUtils.dip2px(getContext(), 2);
				params.gravity = Gravity.CENTER;
				if (this.getChildCount() > 0) {
					this.removeAllViews();
					imageView = null;
				}
				this.addView(textView, params);
			}
			return textView;
		}
		return null;
	}

	public void setImageDrawable(Drawable drawable) {
		View view = getContentView(IMAGE_VIEW);
		if (view != null && view instanceof ImageView) {
			((ImageView) view).setImageDrawable(drawable);
		}
	}

	public void setImageResource(int resId) {
		View view = getContentView(IMAGE_VIEW);
		if (view != null && view instanceof ImageView) {
			((ImageView) view).setImageResource(resId);
		}
	}

	public void setImageBitmap(Bitmap bm) {
		View view = getContentView(IMAGE_VIEW);
		if (view != null && view instanceof ImageView) {
			((ImageView) view).setImageBitmap(bm);
		}
	}

	public void setText(CharSequence text) {
		setText(text, null);
	}

	public void setText(CharSequence text, BufferType type) {
		View view = getContentView(TEXT_VIEW);
		if (view != null && view instanceof TextView) {
			((TextView) view).setText(text, type);
			((TextView) view).setTextSize(textSize);
		}
	}

	public void setTextColor(int color) {
		textColor = color;
		/**
		 * 当前显示的是文本时，则设置字体颜色， 否则缓存到setText的时候设置
		 */
		if (this.getChildCount() == 1) {
			View view = this.getChildAt(getChildCount() - 1);
			if (view != null && view instanceof TextView) {
				((TextView) view).setTextColor(color);
			}
		}
	}

	/**
	 * 该方法的作用:设置文本大小 注意：设置px的值即可，此方法转换成sp值再设置
	 */
	public void setTextSize(float size) {
		textSize = size;
		/**
		 * 当前显示的是文本时，则设置字体大小， 否则缓存到setText的时候设置
		 */
		if (this.getChildCount() == 1) {
			View view = this.getChildAt(getChildCount() - 1);
			if (view != null && view instanceof TextView) {
				((TextView) view).setTextSize(textSize);
			}
		}
	}

	/**
	 * 该方法的作用:设置内容视图与MPImageButton的间距
	 */
	public void setViewPadding(int left, int top, int right, int bottom) {
		if (this.getChildCount() == 1) {
			View view = this.getChildAt(getChildCount() - 1);
			if (view != null) {
				view.setPadding(left, top, right, bottom);
				invalidate();
			}
		}
	}

	private void initBadgeView() {
		if (getContentView() != null) {
			XBadgeView = new XBadgeView(mContext, getContentView());
			XBadgeView.setFocusable(false);
			XBadgeView.setClickable(false);
		}
	}

	/**
	 * 该方法的作用:设置徽章图片背景资源
	 */
	public void setBadgeBackgroudResource(int resid) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setBackgroundResource(resid);
	}

	/**
	 * 该方法的作用:设置徽章视图背景图片
	 */
	public void setBadgeBackgroudDrawable(Drawable drawable) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setBackgroundDrawable(drawable);
	}

	/**
	 * 该方法的作用:设置徽章视图背景颜色
	 */
	public void setBadgeBackgroudColor(int color) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setBackgroundColor(color);
	}

	public void setBadgeDrawable(Drawable drawable) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setImageDrawable(drawable);
	}

	public void setBadgeResource(int resId) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setImageResource(resId);
	}

	/**
	 * 该方法的作用:
	 */
	public void setBadgeBitmap(Bitmap bm) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setImageBitmap(bm);
	}

	/**
	 * 该方法的作用:设置徽章视图文本
	 */
	public void setBadgeText(String text) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setText(text);
	}

	/**
	 * 该方法的作用:设置徽标文本颜色
	 */
	public void setBadgeTextColor(int color) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setTextColor(color);
	}

	/**
	 * 该方法的作用:
	 */
	public void setBadgeTextSize(float size) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setTextSize(size);
	}

	/**
	 * 该方法的作用:设置显示位置
	 */
	public void setBadgePosition(int layoutPosition) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setBadgePosition(layoutPosition);
	}

	/**
	 * 该方法的作用:设置徽章的margin值
	 */
	public void setBadgeMargin(int left, int top, int right, int bottom) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setBadgeMargin(left, top, right, bottom);
	}

	/**
	 * 该方法的作用:设置padding值
	 */
	public void setBadgePadding(int left, int top, int right, int bottom) {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.setPadding(left, top, right, bottom);
	}

	public void showBadgeView() {
		if (XBadgeView == null) {
			initBadgeView();
		}
		XBadgeView.show();
	}

	/**
	 * 该方法的作用:获取内容视图(文本内容或者图片内容)
	 */
	public View getContentView() {
		if (textView != null) {
			return textView;
		} else if (imageView != null) {
			return imageView;
		}
		return null;
	}

}
