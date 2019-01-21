package com.ldl.flowinfo.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

public class BatteryBroadcastReceiver extends BroadcastReceiver {

    private BatteryChangedListener listener;

    public  void setOnBatteryChangedListener(BatteryChangedListener l) {
        listener = l;
    }



    public interface BatteryChangedListener {
        void onBatteryChangedListener(int p);

        void onBatteryDisConnected();

        void onBatteryConnected();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            Log.e("Battery", action);
            switch (action) {
                case Intent.ACTION_BATTERY_CHANGED://电量发生改变
                    if (null != listener) {
                        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                        int percent = (int) (((float) level / scale) * 100);
                        listener.onBatteryChangedListener(percent);
                    }
                    break;
                case Intent.ACTION_POWER_DISCONNECTED://拔出电源
                    if (null != listener) listener.onBatteryDisConnected();
                    break;
                case Intent.ACTION_POWER_CONNECTED:
                    if (null != listener) listener.onBatteryConnected();
                    break;
                default:
                    break;
            }
        }
    }
}
