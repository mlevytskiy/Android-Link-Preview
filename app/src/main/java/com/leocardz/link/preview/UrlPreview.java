package com.leocardz.link.preview;

import android.content.Context;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.library.weblinkpreview2.SourceContent;
import com.library.weblinkpreview2.TextCrawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by max on 03.02.16.
 * When you use this class, you need put ViewHolderUrlPreview with UrlPreview id on your current item view;
 */
public class UrlPreview extends LinearLayout {

    private ImageView imageSet;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private boolean isAttachedToWindow;
    public static Map<String, SourceContent> map = new HashMap<>();

    public UrlPreview(Context context) {
        super(context);
        init(context);
    }

    public UrlPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UrlPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBaselineAligned(false);
        setOrientation(HORIZONTAL);
        inflate(context, R.layout.url_preview_content, this);

        imageSet = (ImageView) findViewById(R.id.image_post_set);
        titleTextView = (TextView) findViewById(R.id.title);
        descriptionTextView = (TextView) findViewById(R.id.description);
    }

    public boolean isAttachedToWindow() {
        return isAttachedToWindow;
    }

    public void setSourceContent(final SourceContent sourceContent) {

        if (TextUtils.isEmpty(sourceContent.getTitle())) {
            setVisibility(GONE);
            return;
        }

        setVisibility(VISIBLE);

        if (sourceContent.getImage() != null) {
            final String imageUrl = StringUtils.fixIfLinkPartial(sourceContent.getImage(), sourceContent.getUrl());
            imageSet.setVisibility(View.GONE);
            try {
                Glide.with(getContext()).load(imageUrl).listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        imageSet.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imageSet.setVisibility(View.VISIBLE);
                        return false;
                    }
                }).into(imageSet);
            } catch (IllegalArgumentException e) {//You cannot start a load for a destroyed activity
                //todo:@m.levytskiy: fix this(catch runtime exception)
                return;
            }
        } else {
            imageSet.setImageBitmap(null);
        }
        titleTextView.setText(sourceContent.getTitle());
        descriptionTextView.setText(sourceContent.getDescription());

    }

    public void setUrls(URLSpan[] urlSpans, final String text, final ListView listView) {
        if (urlSpans == null || urlSpans.length == 0) {
            setVisibility(GONE);
            return;
        }

        if (listView.getContext() == null) {
            //our activity stay in back stack
            return;
        }

        final List<String> webLinks = new ArrayList<String>();
        for (URLSpan urlSpan : urlSpans) {
            String urlString = urlSpan.getURL();
            if (!StringUtils.hasEmail(urlString)) {
                webLinks.add(urlString);
            }
        }

        if (webLinks.isEmpty()) {
            setVisibility(GONE);
            return;
        }

        setVisibility(VISIBLE);

        final SourceContent sourceContent;

        if ((sourceContent = map.get(text)) != null) {
            if (TextUtils.isEmpty(sourceContent.getUrl())) {
                setVisibility(GONE);
            } else {
                setSourceContent(sourceContent);
                if (isAttachedToWindow) {
                    setSourceContent(sourceContent);
                } else {
                    this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setSourceContent(sourceContent);
                        }
                    }, 1000);
                }
            }
        } else {
            map.put(text, new SourceContent());
            setVisibility(GONE);
            new TextCrawler().makePreview(new LinkPreviewCallbackImpl(text, listView) {

                    @Override
                    public void onPre() {
                        //do nothing
                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean b) {
                        super.onPos(sourceContent, b);
                    }
                }, webLinks.get(0));
        }
    }

    public void onDetachedFromWindow() {
        Glide.clear(this);
        super.onDetachedFromWindow();
        isAttachedToWindow = false;
    }

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttachedToWindow = true;
    }

    public static class ViewHolderUrlPreview {

        public UrlPreview urlPreview;
        public TextView textView;

    }
}
