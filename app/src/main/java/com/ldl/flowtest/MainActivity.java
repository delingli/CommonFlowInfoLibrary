package com.ldl.flowtest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ldl.flowinfo.activity.ChargingInfoActivity;

public class MainActivity extends AppCompatActivity implements ChargingInfoActivity.OnSetClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChargingInfoActivity.startChargingInfoActivity(this, this);
        finish();
    }

    @Override
    public void onSetClick() {
        Toast.makeText(this, "回來了？", Toast.LENGTH_SHORT).show();
    }
}
