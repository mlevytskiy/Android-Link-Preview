package com.library.weblinkpreview2;

import android.content.Context;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.weblinkpreview2.linkPreviewContainerImpl.LinkPreview;
import com.library.weblinkpreview2.linkPreviewContainerImpl.OnAddTextViewListener;
import com.library.weblinkpreview2.linkPreviewContainerImpl.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by max on 11.02.16.
 */
public class LinkPreviewContainer extends LinearLayout {

    private LinkPreview linkPreview;
    private AllVisiblePreviewContainers previewContainers;
    private TextView textView;

    public LinkPreviewContainer(Context context) {
        super(context);
        init();
    }

    public LinkPreviewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinkPreviewContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LinkPreview getLinkPreview() {
        return linkPreview;
    }

    public TextView getTextView() {
        return textView;
    }

    private void init() {
        setOrientation(VERTICAL);
    }

    public void setAllVisiblePreviewContainers(AllVisiblePreviewContainers previewContainers) {
        this.previewContainers = previewContainers;
        if (linkPreview != null) {
            linkPreview.setAllVisiblePreviewContainers(previewContainers);
        }
    }

    public void textWasChanged(TextView textView) {
        URLSpan[] urlSpanArray = textView.getUrls();
        if ((urlSpanArray != null) && (urlSpanArray.length != 0)) {
            List<URLSpan> webLinks = new ArrayList<>();
            if (isNeedShowLinkPreview(urlSpanArray, webLinks)) {
                linkPreview.loadLink(webLinks.get(0).getURL(), textView.getText().toString());
                linkPreview.setVisibility(VISIBLE);
            } else {
                linkPreview.setVisibility(GONE);
            }
        } else {
            linkPreview.setVisibility(GONE);
        }

    }

    private boolean isNeedShowLinkPreview(URLSpan[] urlSpanArray, List<URLSpan> webLinks) {
        boolean hasWebLink = false;
        for (int i = 0; i < urlSpanArray.length; i++) {
            if (StringUtils.hasEmail(urlSpanArray[i].getURL())) {
                //do nothing
            } else {
                hasWebLink = true;
                webLinks.add(urlSpanArray[i]);
            }
        }
        return hasWebLink;
    }

    private LinkPreview attachHiddenLinkPreview(Context context) {
        linkPreview = new LinkPreview(context);
        addView(linkPreview);
        return linkPreview;
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setOnHierarchyChangeListener(new OnAddTextViewListener() {
            @Override
            protected void onTextViewAdded(TextView textView) {
                if (getChildCount() == 1) { // Is the textView first?
                    LinkPreviewContainer.this.textView = textView;
                    LinkPreview linkPreview = attachHiddenLinkPreview(getContext());
                    linkPreview.setAllVisiblePreviewContainers(previewContainers);
                } else {
                    //do nothing
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        setOnHierarchyChangeListener(null); // because HierarhyChange listener inner non-static class, so it hold
                                            // link to this view. So we prevent memory leak.
        super.onDetachedFromWindow();
    }

}
