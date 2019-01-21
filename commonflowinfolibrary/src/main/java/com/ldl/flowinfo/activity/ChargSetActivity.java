package com.ldl.flowinfo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ldl.flowinfo.R;

public class ChargSetActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chargingset);
        initView();

    }

    private void initView() {
        SwitchCompat switchcompat = (SwitchCompat) findViewById(R.id.sw_ofOn);
        switchcompat.setChecked(getFirstState());
        switchcompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changeSwitch(b);
            }
        });
        findViewById(R.id.tv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void changeSwitch(boolean b) {
    }

    public boolean getFirstState() {
        return false;
    }
}
