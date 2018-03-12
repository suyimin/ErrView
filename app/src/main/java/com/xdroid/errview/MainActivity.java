package com.xdroid.errview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.xdroid.library.widget.XBadgeView;
import com.xdroid.library.widget.XPrompt;
import com.xdroid.library.widget.XDialog;
import com.xdroid.library.widget.XEmptyView;
import com.xdroid.library.widget.XLoadingView;
import com.xdroid.library.widget.XToast;

public class MainActivity extends Activity implements View.OnClickListener, XEmptyView.OnRetryListener {

    private TextView tvTSnackbar;
    private TextView tvLoadingView;
    private TextView tvEmptyView;
    private XLoadingView loadingView;
    private XEmptyView emptyView;
    private XBadgeView XBadgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTSnackbar = findViewById(R.id.tvToast);
        tvTSnackbar.setOnClickListener(this);
        tvLoadingView = findViewById(R.id.tvLoadingView);
        tvLoadingView.setOnClickListener(this);
        tvEmptyView = findViewById(R.id.tvEmptyView);
        tvEmptyView.setOnClickListener(this);
        loadingView = findViewById(R.id.loadingView);
        emptyView = findViewById(R.id.emptyView);
        emptyView.setOnRetryListener(this);
        XBadgeView = new XBadgeView(this, tvTSnackbar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvToast:
                showDialog(this, R.string.app_name, R.string.btn_comfirm, R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XToast.makeText(MainActivity.this, "错错错，是我的错～～～～", XPrompt.WARNING).show();
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XBadgeView.setBadgePosition(XBadgeView.POSITION_TOP_RIGHT);
                        XBadgeView.setText("99+");
                        XBadgeView.show();
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.tvLoadingView:
                loadingView.setVisibility(View.GONE);
                emptyView.showView(XEmptyView.VIEW_TYPE_NO_NETWORK, "无网络～～～～", "");
                emptyView.setVisibility(View.VISIBLE);
                break;
            case R.id.tvEmptyView:
                loadingView.setVisibility(View.GONE);
                emptyView.showView(XEmptyView.VIEW_TYPE_EMPTY, "无数据～～～～", "");
                emptyView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onRetry() {
        emptyView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
    }

    public static Dialog showDialog(Context context, int bodyStr, int okStr, int cancelStr, DialogInterface.OnClickListener ok, DialogInterface.OnClickListener cancel) {
        XDialog dialog = new XDialog(context);
        dialog.setTitleVisibility(View.GONE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setBodyText(context.getString(bodyStr));
        if (cancel != null) {
            dialog.setLeftButton(context.getString(cancelStr), cancel);
        } else {
            dialog.setLeftButton(context.getString(cancelStr), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        dialog.setRightButton(context.getString(okStr), ok);
        dialog.setRightButtonColor(context.getResources().getColor(R.color.image_picker_dialog_i_know));
        dialog.show();
        return dialog;
    }
}
