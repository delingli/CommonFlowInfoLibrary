package com.ldl.lockscreeninfo;

import java.io.Serializable;

/**
 * create by ldl2018/9/17 0017
 */

public class PluginNotificationInfo implements Serializable {

    /**
     * icon : http://ali.cdn.pys5.com/cayt/ab8d0e72.png
     * linkType : web
     * title : 测试一键换机wifi触发通知栏
     * link : http://www.baidu.com
     * notifyId : 2002
     * s_rpt : http://101.132.170.187:7701/dgfly_rpt.php?act=s&adid=ab8d0e72&cp=AA511&did=867271035653434&aid=a3fecd90a89c4429
     * content :
     * c_rpt : http://101.132.170.187:7701/dgfly_rpt.php?act=c&adid=ab8d0e72&cp=AA511&did=867271035653434&aid=a3fecd90a89c4429
     */

    private String icon;
    private String linkType;
    private String title;
    private String link;
    private String notifyId;
    private String s_rpt;
    private String content;
    private String c_rpt;
    private String d_rpt;
    private String dc_rpt;
    private String i_rpt;
    private String a_rpt;
    private String dp_rpt;

    public String getD_rpt() {
        return d_rpt;
    }

    public void setD_rpt(String d_rpt) {
        this.d_rpt = d_rpt;
    }

    public String getDc_rpt() {
        return dc_rpt;
    }

    public void setDc_rpt(String dc_rpt) {
        this.dc_rpt = dc_rpt;
    }

    public String getI_rpt() {
        return i_rpt;
    }

    public void setI_rpt(String i_rpt) {
        this.i_rpt = i_rpt;
    }

    public String getA_rpt() {
        return a_rpt;
    }

    public void setA_rpt(String a_rpt) {
        this.a_rpt = a_rpt;
    }

    public String getDp_rpt() {
        return dp_rpt;
    }

    public void setDp_rpt(String dp_rpt) {
        this.dp_rpt = dp_rpt;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(String notifyId) {
        this.notifyId = notifyId;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getS_rpt() {
        return s_rpt == null ? "" : s_rpt;
    }

    public void setS_rpt(String s_rpt) {
        this.s_rpt = s_rpt;
    }

    public String getC_rpt() {
        return c_rpt == null ? "" : c_rpt;
    }

    public void setC_rpt(String c_rpt) {
        this.c_rpt = c_rpt;
    }
}
