package com.leocardz.link.preview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.library.weblinkpreview2.AllVisiblePreviewContainers;
import com.library.weblinkpreview2.LinkPreviewContainer;

import java.util.List;

/**
 * Created by max on 04.02.16.
 */
public class ManyLinksAdapter2 extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<String> list;
    private ListView listView;

    public ManyLinksAdapter2(Context context, List<String> list, ListView listView) {
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
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item2, parent, false);
            convertView.setTag(viewHolder);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text_view_for_web_link_preview);
            viewHolder.container = (LinkPreviewContainer) convertView;
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String str = getItem(position);
        viewHolder.textView.setText(str);
        viewHolder.container.setAllVisiblePreviewContainers(new AllVisiblePreviewContainers(listView) {

            @Override
            public LinkPreviewContainer getPreviewContainerFromItem(View item) {
                return (LinkPreviewContainer) item;
            }

        });
        viewHolder.container.textWasChanged(viewHolder.textView);
        return convertView;
    }

    private static class ViewHolder {

        public LinkPreviewContainer container;
        public TextView textView;

    }

}
