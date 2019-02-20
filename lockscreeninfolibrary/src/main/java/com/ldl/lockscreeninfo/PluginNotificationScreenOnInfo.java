package com.ldl.lockscreeninfo;

import java.io.Serializable;

/**
 * create by ldl2018/9/17 0017
 */

public class PluginNotificationScreenOnInfo implements Serializable {
    public String s_rpt;
    public String c_rpt;
    public String source;

    public PluginNotificationScreenOnInfo(String s_rpt, String c_rpt, String source) {
        this.s_rpt = s_rpt;
        this.c_rpt = c_rpt;
        this.source = source;
    }

    public PluginNotificationScreenOnInfo() {
    }
}
