package com.library.weblinkpreview2.linkPreviewContainerImpl;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.library.weblinkpreview2.AllVisiblePreviewContainers;
import com.library.weblinkpreview2.LinkPreviewContainer;
import com.library.weblinkpreview2.R;
import com.library.weblinkpreview2.TextCrawler;
import com.library.weblinkpreview2.pojo.LinkPreviewPojo;
import com.library.weblinkpreview2.pojo.LoadingStatus;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by max on 15.02.16.
 */
public class LinkPreview extends LinearLayout {

    private static HashMap<String, LinkPreviewPojo> pojoHashMaps = new HashMap<>();
    private AllVisiblePreviewContainers previewContainers;

    private TextView title;
    private TextView description;
    private ImageView image;

    public LinkPreview(Context context) {
        super(context);
        init(context);
    }

    public LinkPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LinkPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
        inflate(context, R.layout.web_link_preview, this);
        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        image = (ImageView) findViewById(R.id.image_post_set);
    }

    public void setAllVisiblePreviewContainers(AllVisiblePreviewContainers previewContainers) {
        this.previewContainers = previewContainers;
    }

    public void loadLink(final String webLink, final String text) {
        LinkPreviewPojo pojo = pojoHashMaps.get(text);
        if (pojo == null) {
            LinkPreviewPojo loadingPojo = new LinkPreviewPojo();
            loadingPojo.setLoadingStatus(LoadingStatus.loading);
            pojoHashMaps.put(text, loadingPojo);
            showWhenWebLinkIsLoading();
            new AsyncTask<String, Void, LinkPreviewPojo>() {

                @Override
                protected LinkPreviewPojo doInBackground(String... params) {
                    LinkPreviewPojo pojo = new LinkPreviewPojo();
                    pojo.setBaseUrl(params[0]);
                    TextCrawler.fillSourceContent(pojo, pojo.getBaseUrl());
                    pojo.setLoadingStatus(TextUtils.isEmpty(pojo.getTitle()) ? LoadingStatus.error : LoadingStatus.success);
                    return pojo;
                }

                @Override
                protected void onPostExecute(LinkPreviewPojo result) {
                    pojoHashMaps.put(text, result);
                    showWebLinkOnUI(findAllViewByUrl(webLink), result);
                }

            }.execute(webLink);
        } else {
            if (isLoading(pojo)) {
                showWhenWebLinkIsLoading();
            } else {
                showWebLinkOnUI(pojo);
            }

        }
    }

    private boolean isLoading(LinkPreviewPojo pojo) {
        return (pojo.getLoadingStatus() == LoadingStatus.loading);
    }

    private void showWebLinkOnUI(List<LinkPreview> list, LinkPreviewPojo pojo) {
        for (LinkPreview linkPreview : list) {
            linkPreview.showWebLinkOnUI(pojo);
        }
    }

    private void showWebLinkOnUI(LinkPreviewPojo pojo) {
        if (pojo.getLoadingStatus() == LoadingStatus.success) {
            title.setText(pojo.getTitle());
            description.setText(pojo.getDescription());
            Picasso.with(image.getContext()).load(pojo.getImageUrl()).into(image);
        } else if (pojo.getLoadingStatus() == LoadingStatus.error) {
            title.setText("Error");
            description.setText("");
            image.setImageBitmap(null);
        } else {
            //do nothing
        }
    }

    private void showWhenWebLinkIsLoading() {
        title.setText("Loading...");
        description.setText("");
        image.setImageBitmap(null);
    }

    private List<LinkPreview> findAllViewByUrl(String url) {
        List<LinkPreview> result = new ArrayList<>();

        for (LinkPreviewContainer linkPreviewContainer : previewContainers.getAll()) {
            LinkPreview linkPreview = linkPreviewContainer.getLinkPreview();
            TextView textView = linkPreviewContainer.getTextView();
            String urlInChild = (textView.getUrls().length == 0) ? null : textView.getUrls()[0].getURL();

            if (TextUtils.equals(urlInChild, url)) {
                result.add(linkPreview);
            } else {
                //do nothing
            }
        }

        return result;
    }
}
