package com.leocardz.link.preview;

import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.library.weblinkpreview2.LinkPreviewCallback;
import com.library.weblinkpreview2.SourceContent;

/**
 * Created by max on 04.02.16.
 */
public abstract class LinkPreviewCallbackImpl implements LinkPreviewCallback {

    private String text;
    private ListView listView;

    public LinkPreviewCallbackImpl(String text, ListView listView) {
        this.text = text;
        this.listView = listView;
    }

    @Override
    public void onPos(SourceContent sourceContent, boolean b) {
        int count = listView.getChildCount();
        UrlPreview.map.put(text, sourceContent);
        for (int i = 0; i < count; i++) {
            View view = listView.getChildAt(i);
            String currentText = ((TextView) view.findViewById(R.id.text_view)).getText().toString();
            UrlPreview urlPreview = (UrlPreview) view.findViewById(R.id.url_preview);
            if (TextUtils.equals(text, currentText)) {
                urlPreview.setSourceContent(sourceContent);
                urlPreview.setVisibility(View.VISIBLE);
            }
        }
    }
}
