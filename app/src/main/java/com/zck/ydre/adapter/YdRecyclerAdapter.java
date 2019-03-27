package com.zck.ydre.adapter;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.squareup.picasso.Picasso;
import com.zck.ydre.R;
import com.zck.ydre.util.ImageInfo;


public class YdRecyclerAdapter extends BaseQuickAdapter<ImageInfo, BaseViewHolder> {

    public YdRecyclerAdapter() {
        super(R.layout.main_recycler_item, null);
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageInfo item) {
        Picasso.get().load(item.getPreview()).placeholder(R.drawable.test).into((ImageView) helper.getView(R.id.main_recycler_item_iv));
    }
}
