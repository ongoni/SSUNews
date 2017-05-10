package com.ssunews.ongoni.ssunews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsItemAdapter extends BaseAdapter {

    private static final class ViewHolder {
        private TextView viewTitle;
        private TextView viewDescription;
        private TextView viewPubDate;
    }

    private final List<Article> data;
    private final Context context;
    private final LayoutInflater inflater;

    NewsItemAdapter(Context context, List<Article> data) {
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        Article art = (Article) getItem(position);
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
            v = inflater.inflate(R.layout.list_item, parent, false);

            holder.viewTitle = (TextView) v.findViewById(R.id.title);
            holder.viewDescription = (TextView) v.findViewById(R.id.description);
            holder.viewPubDate = (TextView) v.findViewById(R.id.pubDate);

            holder.viewTitle.setText(art.title);
            holder.viewDescription.setText(art.description);
            holder.viewPubDate.setText(art.pubDate);

            v.setTag(holder);

        } else {
            v = convertView;
            holder = (ViewHolder) convertView.getTag();
            holder.viewTitle.setText(art.title);
            holder.viewDescription.setText(art.description);
            holder.viewPubDate.setText(art.pubDate);
        }

        return v;
    }
}