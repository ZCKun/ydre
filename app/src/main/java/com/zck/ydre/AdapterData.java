package com.zck.ydre;

import com.zck.ydre.util.ImageInfo;
import com.zck.ydre.util.SideBar;

import java.util.ArrayList;

public class AdapterData {

    private ArrayList<ImageInfo> imageInfos;
    private ArrayList<SideBar> sideBars;

    public AdapterData(ArrayList<ImageInfo> imageInfos, ArrayList<SideBar> sideBars) {
        this.imageInfos = imageInfos;
        this.sideBars = sideBars;
    }

    public ArrayList<ImageInfo> getImageInfos() {
        return imageInfos;
    }

    public ArrayList<SideBar> getSideBars() {
        return sideBars;
    }
}
