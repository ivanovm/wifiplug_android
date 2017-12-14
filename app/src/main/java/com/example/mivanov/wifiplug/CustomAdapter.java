package com.example.mivanov.wifiplug;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by mivanov on 14/12/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context context;
    List<RowItem> rowItems;

    CustomAdapter(Context context, List<RowItem> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int i) {
        return rowItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return rowItems.indexOf(getItem(i));
    }

    public class ViewHolder {
        ImageView status_pic;
        TextView plug_id;

        public void updateView(RowItem rowItem) {
            plug_id.setText(rowItem.getPlugId());

        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();

            holder.plug_id = (TextView) view.findViewById(R.id.plug_id);
            holder.status_pic = (ImageView) view.findViewById(R.id.status_pic);

            RowItem row_pos = rowItems.get(i);

            holder.plug_id.setText(row_pos.getPlugId());
            // TODO: holder.status_pic.setImageResource();

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        return view;
    }
}
