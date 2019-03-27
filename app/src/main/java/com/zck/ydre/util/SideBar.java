package com.zck.ydre.util;

import android.os.Parcel;
import android.os.Parcelable;


public class SideBar implements Parcelable {

    private String title;
    private String link;

    public static final Parcelable.Creator<SideBar> CREATOR = new Parcelable.Creator<SideBar>(){
        @Override
        public SideBar createFromParcel(Parcel source) {
            return new SideBar(source);
        }

        @Override
        public SideBar[] newArray(int size) {
            return new SideBar[size];
        }
    };

    public SideBar(Parcel in) {
        super();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        title = in.readString();
        link = in.readString();
    }

    public SideBar(String title, String link) {
        this.link = link;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
    }
}
