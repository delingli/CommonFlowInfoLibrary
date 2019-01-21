package com.ldl.flowinfo;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ldl.flowinfo.activity.ChargingInfoActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.ldl.flowinfo.test", appContext.getPackageName());

//        Intent intent = new Intent(appContext, ChargingInfoActivity.class);
//        appContext.startActivity(intent);
        ChargingInfoActivity.startChargingInfoActivity(appContext, new ChargingInfoActivity.OnSetClickListener() {
            @Override
            public void onSetClick() {
                Log.d("ldl", "回调...");
            }
        });

    }
}
