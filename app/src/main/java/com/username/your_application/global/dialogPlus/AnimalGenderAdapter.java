package com.username.your_application.global.dialogPlus;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.username.your_application.R;

public class AnimalGenderAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    private boolean isGrid;

    public AnimalGenderAdapter(Context context, boolean isGrid) {
        layoutInflater = LayoutInflater.from(context);
        this.isGrid = isGrid;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view = convertView;

        if (view == null) {
            if (isGrid) {
                view = layoutInflater.inflate(R.layout.simple_grid_item, parent, false);
            } else {
                view = layoutInflater.inflate(R.layout.simple_list_item, parent, false);
            }

            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) view.findViewById(R.id.text_view);
            viewHolder.imageView = (ImageView) view.findViewById(R.id.image_view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        Context context = parent.getContext();
        switch (position) {
            case 0:
                viewHolder.textView.setText(context.getString(R.string.male));
                viewHolder.imageView.setImageResource(R.drawable.ic_mars);
                break;
            case 1:
                viewHolder.textView.setText(context.getString(R.string.female));
                viewHolder.imageView.setImageResource(R.drawable.ic_venus);
                break;
        }

        return view;
    }

    static class ViewHolder {
        TextView textView;
        ImageView imageView;
    }
}
