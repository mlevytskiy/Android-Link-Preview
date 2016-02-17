package com.library.weblinkpreview2;

import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 17.02.16.
 */
public abstract class AllVisiblePreviewContainers {

    protected ListView listView;

    public AllVisiblePreviewContainers(ListView listView) {
        this.listView = listView;
    }

    public final List<LinkPreviewContainer> getAll() {
        List<LinkPreviewContainer> list = new ArrayList<LinkPreviewContainer>();
        for (int i = 0; i < listView.getChildCount(); i++) {
            LinkPreviewContainer container = getPreviewContainerFromItem(listView.getChildAt(i));
            list.add(container);
        }
        return list;
    }

    public abstract LinkPreviewContainer getPreviewContainerFromItem(View item);

}
