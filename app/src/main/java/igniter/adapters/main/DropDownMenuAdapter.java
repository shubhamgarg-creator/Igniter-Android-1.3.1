package igniter.adapters.main;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.main
 * @category DropDownMenuAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.obs.CustomTextView;

import igniter.R;

/*****************************************************************
 Adapter for drop down menu
 ****************************************************************/

public class DropDownMenuAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private String[] listItems;
    private int[] images;

    public DropDownMenuAdapter(Context context, int layout, int[] images, String[] listItems) {
        this.context = context;
        this.layout = layout;
        this.images = images;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.length;
    }

    @Override
    public Object getItem(int position) {
        return listItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(layout, parent, false);
            holder.tvListItem = (CustomTextView) convertView.findViewById(R.id.tv_list_item);
            holder.tvListItem.setId(position);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvListItem.setText(listItems[position]);
        if (images != null && position < images.length) {
            holder.tvListItem.setCompoundDrawablesWithIntrinsicBounds(images[position], 0, 0, 0);
            holder.tvListItem.setCompoundDrawablePadding(10);
        } else {
            holder.tvListItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        return convertView;
    }

    private class ViewHolder {
        CustomTextView tvListItem;
    }
}
