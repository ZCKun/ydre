package com.zck.ydre.util;

import android.os.Parcel;
import android.os.Parcelable;

public class ImageInfo implements Parcelable {

    private String title;
    private String preview;
    private String source;

    public ImageInfo(Parcel in) {
        super();
        readFromParcel(in);
    }

    public ImageInfo(String title, String preview, String source) {
        this.title = title;
        this.preview = preview;
        this.source = source;
    }

    public void readFromParcel(Parcel in) {
        title = in.readString();
        preview = in.readString();
        source = in.readString();
    }

    public static final Parcelable.Creator<ImageInfo> CREATOR = new Parcelable.Creator<ImageInfo>(){
        @Override
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public String getPreview() {
        return preview;
    }

    public String getSource() {
        return source;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(preview);
        dest.writeString(source);
    }
}
