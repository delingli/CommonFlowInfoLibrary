package com.ldl.flowinfo.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ldl.flowinfo.R;
import com.ldl.flowinfo.broadcast.BatteryBroadcastReceiver;
import com.ldl.flowinfo.swiplayout.SwipeBackActivity;
import com.ldl.flowinfo.swiplayout.SwipeBackLayout;
import com.ldl.flowinfo.utils.UIUtils;
import com.sant.chafer.ChaferFragment;

import java.util.Calendar;
import java.util.Date;

public class ChargingInfoActivity extends SwipeBackActivity {

    private PopupWindow popupWindow;
    private BatteryBroadcastReceiver receiver;
    private TextView tv_electricity;
    private ImageView iv_electricity_state;
    private ImageView iv_lignt;
    private TextView tv_unlock;
    private TextView tv_date;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charginginfo);
        initPoPupWindow();
        initView();
    }

    private void initWindows() {
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
        WindowManager.LayoutParams wmParams = getWindow().getAttributes();  //获取对话框当前的参数值
        wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
//        wmParams.alpha = 1.0f;      //设置本身透明度
//        wmParams.dimAmount = 0.0f;
//        wmParams.format = PixelFormat.RGBA_8888; //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        // 调整悬浮窗显示的停靠位置为左侧置顶
//        wmParams.gravity = Gravity.CENTER;
        // 设置悬浮窗口长宽数据
//        wmParams.height = WindowManager.LayoutParams.MATCH_PARENT;
//        wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        wmParams.flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD;
        getWindow().setAttributes(wmParams);     //设置生效
    }

    private int left, right;

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        ImageView iv_setting = (ImageView) findViewById(R.id.iv_setting);
        iv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(v, -100, 0);
                }
            }
        });
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_unlock = (TextView) findViewById(R.id.tv_unlock);
        iv_lignt = (ImageView) findViewById(R.id.iv_lignt);
        iv_electricity_state = (ImageView) findViewById(R.id.iv_electricity_state);
        final FrameLayout rl_unlock = (FrameLayout) findViewById(R.id.rl_unlock);
        tv_electricity = (TextView) findViewById(R.id.tv_electricity);
        ChaferFragment fragment = new ChaferFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ChaferFragment.KEY_GONE_TOP, true);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, fragment, ChaferFragment.class.getSimpleName()).commit();
        receiver = new BatteryBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(receiver, filter);
        receiver.setOnBatteryChangedListener(new BatteryBroadcastReceiver.BatteryChangedListener() {
            @Override
            public void onBatteryChangedListener(int p) {
                setPercent(p);
            }

            @Override
            public void onBatteryDisConnected() {

            }

            @Override
            public void onBatteryConnected() {

            }
        });
        setdate(tv_date);
        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.setEnableGesture(true);//关闭右滑返回上一级
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
//        swipeBackLayout.saveTrackingMode(SwipeBackLayout.EDGE_LEFT);
        tv_unlock.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rl_unlock.setVisibility(View.VISIBLE);
                rl_unlock.setBackgroundColor(Color.parseColor("#000000"));
                rl_unlock.setAlpha(0.5f);
                int[] location = new int[2];
                tv_unlock.getLocationOnScreen(location);
                left = tv_unlock.getLeft();
                right = tv_unlock.getRight();
                tv_unlock.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                ObjectAnimator translationX = ObjectAnimator.ofFloat(iv_lignt, "translationX", left, right);
                translationX.setRepeatMode(ValueAnimator.RESTART);
                translationX.setInterpolator(new LinearInterpolator());
                translationX.setDuration(3000);
                translationX.setRepeatCount(ValueAnimator.INFINITE);
                translationX.start();
            }
        });
        rl_unlock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float x1 = 0f;
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x1 = motionEvent.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x2 = motionEvent.getX();
                        if (x2 - x1 > 500) {
                            Log.d("Daling", "x1:"+x1+"x2:"+x2+"距离:"+(x2-x1));
                            finish();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public static void startChargingInfoActivity(Context context, OnSetClickListener listener) {
        Intent intent = new Intent(context, ChargingInfoActivity.class);
        context.startActivity(intent);
        mListener = listener;
    }

    public static void startChargingInfoActivity(Context context) {
        Intent intent = new Intent(context, ChargingInfoActivity.class);
        context.startActivity(intent);
    }

    private static OnSetClickListener mListener;

    public interface OnSetClickListener {
        void onSetClick();
    }

    private void initPoPupWindow() {
        View inflate = View.inflate(ChargingInfoActivity.this, R.layout.popupwindow_set, null);
        popupWindow = new PopupWindow(inflate, UIUtils.dip2px(getApplicationContext(), 108), UIUtils.dip2px(getApplicationContext(), 64), true);
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        popupWindow.setTouchable(true);
        inflate.findViewById(R.id.tv_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSetClick();
                }
                onSetItemClock();
                popupWindow.dismiss();
                finish();

            }
        });
        inflate.findViewById(R.id.tv_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                finish();
            }
        });
    }

    public void onSetItemClock() {

    }

    public void setdate(TextView tvdate) {
        Calendar cal = Calendar.getInstance();
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DATE);
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        tvdate.setText(d + "/" + m + " " + weekDays[w]);
    }

    private void setPercent(int p) {
        tv_electricity.setText(p + "%");
        if (0 <= p && p <= 10) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_10);
        }
        if (11 <= p && p <= 20) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_20);
        }
        if (21 <= p && p <= 30) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_30);
        }

        if (31 <= p && p <= 40) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_40);
        }
        if (41 <= p && p <= 50) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_50);
        }
        if (51 <= p && p <= 60) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_60);
        }


        if (61 <= p && p <= 70) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_70);
        }
        if (71 <= p && p <= 80) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_80);
        }
        if (81 <= p && p <= 90) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_90);
        }
        if (91 <= p && p <= 100) {
            iv_electricity_state.setImageResource(R.drawable.dianliang_100);
        }


    }
}
