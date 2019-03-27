package com.zck.ydre.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.zck.ydre.R;
import com.zck.ydre.util.SideBar;


public class SideBarRecyclerAdapter extends BaseQuickAdapter<SideBar, BaseViewHolder> {

    public SideBarRecyclerAdapter() {
        super(R.layout.horizontal_recycler_item, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, SideBar item) {
        helper.setText(R.id.tv, item.getTitle());
    }
}
