package com.ldl.lockscreeninfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.sant.api.moives.MVITVideo;


public class ScreenOnDataInfo implements Parcelable {
    private String tag;
    private ScreenOnData sod;
    private String ksp_url;
    private String jgz_href;
    private MVITVideo mvItem;
    public String s_rpt;
    public String c_rpt;

    public ScreenOnDataInfo() {
    }

    public ScreenOnDataInfo(String tag, ScreenOnData sod, String ksp_url, String jgz_href, MVITVideo mvItem, String s_rpt, String c_rpt) {
        this.tag = tag;
        this.sod = sod;
        this.ksp_url = ksp_url;
        this.jgz_href = jgz_href;
        this.mvItem = mvItem;
        this.s_rpt = s_rpt;
        this.c_rpt = c_rpt;
    }

    protected ScreenOnDataInfo(Parcel in) {
        tag = in.readString();
        sod = in.readParcelable(ScreenOnData.class.getClassLoader());
        ksp_url = in.readString();
        jgz_href = in.readString();
        mvItem = in.readParcelable(MVITVideo.class.getClassLoader());
        s_rpt = in.readString();
        c_rpt = in.readString();
    }

    public static final Creator<ScreenOnDataInfo> CREATOR = new Creator<ScreenOnDataInfo>() {
        @Override
        public ScreenOnDataInfo createFromParcel(Parcel in) {
            return new ScreenOnDataInfo(in);
        }

        @Override
        public ScreenOnDataInfo[] newArray(int size) {
            return new ScreenOnDataInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tag);
        dest.writeParcelable(sod, flags);
        dest.writeString(ksp_url);
        dest.writeString(jgz_href);
        dest.writeParcelable(mvItem, flags);
        dest.writeString(s_rpt);
        dest.writeString(c_rpt);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ScreenOnData getSod() {
        return sod;
    }

    public void setSod(ScreenOnData sod) {
        this.sod = sod;
    }

    public String getKsp_url() {
        return ksp_url;
    }

    public void setKsp_url(String ksp_url) {
        this.ksp_url = ksp_url;
    }

    public String getJgz_href() {
        return jgz_href;
    }

    public void setJgz_href(String jgz_href) {
        this.jgz_href = jgz_href;
    }

    public MVITVideo getMvItem() {
        return mvItem;
    }

    public void setMvItem(MVITVideo mvItem) {
        this.mvItem = mvItem;
    }

    public String getS_rpt() {
        return s_rpt;
    }

    public void setS_rpt(String s_rpt) {
        this.s_rpt = s_rpt;
    }

    public String getC_rpt() {
        return c_rpt;
    }

    public void setC_rpt(String c_rpt) {
        this.c_rpt = c_rpt;
    }
}
