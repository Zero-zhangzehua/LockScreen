package com.example.lockscreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lockscreen.R;
import com.example.lockscreen.vo.MenuContent;

import java.util.List;

/**
 * Created by S9023192 on 2019/8/13.
 */

public class MenuContentAdapter extends BaseAdapter {

    private Context mContext;
    private List<MenuContent> mList;

    public MenuContentAdapter(Context mContext, List<MenuContent> mList) {
        super();
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mList != null) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return mList.get(position).getImageId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        MenuContent menuContent = (MenuContent) getItem(position);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.imageView = (ImageView) convertView.findViewById(R.id.item_imageview);
        viewHolder.textView = (TextView) convertView.findViewById(R.id.item_textview);
        viewHolder.imageView.setImageResource(menuContent.getImageId());
        viewHolder.textView.setText(menuContent.getText());
        return convertView;
    }

    class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}
