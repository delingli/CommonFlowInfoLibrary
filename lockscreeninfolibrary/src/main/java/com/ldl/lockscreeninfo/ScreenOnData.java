package com.ldl.lockscreeninfo;

import android.os.Parcel;
import android.os.Parcelable;

public class ScreenOnData implements Parcelable {
    public String title;
    public String desc;
    public String pic_url;
    public String tag;

    public ScreenOnData(String title, String desc, String pic_url, String tag) {
        this.title = title;
        this.desc = desc;
        this.tag = tag;
        this.pic_url = pic_url;

    }

    protected ScreenOnData(Parcel in) {
        title = in.readString();
        desc = in.readString();
        pic_url = in.readString();
        tag = in.readString();
    }

    public static final Creator<ScreenOnData> CREATOR = new Creator<ScreenOnData>() {
        @Override
        public ScreenOnData createFromParcel(Parcel in) {
            return new ScreenOnData(in);
        }

        @Override
        public ScreenOnData[] newArray(int size) {
            return new ScreenOnData[size];
        }
    };

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(pic_url);
        dest.writeString(tag);
    }
}
