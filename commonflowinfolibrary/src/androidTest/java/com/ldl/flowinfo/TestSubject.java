package com.ldl.flowinfo;

import android.test.InstrumentationTestCase;

import com.ldl.flowinfo.activity.ChargingInfoActivity;

public class TestSubject extends InstrumentationTestCase {
    public void testChargingInfoActivity() {
        launchActivity("com.ldl.flowinfo", ChargingInfoActivity.class, null);
    }
}
