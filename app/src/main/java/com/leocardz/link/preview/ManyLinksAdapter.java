package com.leocardz.link.preview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by max on 04.02.16.
 */
public class ManyLinksAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<String> list;
    private ListView listView;

    public ManyLinksAdapter(Context context, List<String> list, ListView listView) {
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item, null);
        } else {
            //do nothing
        }
        TextView textView = (TextView) convertView.findViewById(R.id.text_view);
        String str = getItem(position);
        textView.setText(str);
        ((UrlPreview) convertView.findViewById(R.id.url_preview)).setUrls(textView.getUrls(), str, listView);
        return convertView;
    }

}
