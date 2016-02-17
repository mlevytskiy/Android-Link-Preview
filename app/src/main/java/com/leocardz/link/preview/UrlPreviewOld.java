package com.leocardz.link.preview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.library.weblinkpreview2.SourceContent;
import com.library.weblinkpreview2.TextCrawler;

/**
 * Created by max on 03.02.16.
 */
public class UrlPreviewOld extends LinearLayout {

    private LinearLayout infoWrap;
    private LinearLayout titleWrap;
    private LinearLayout thumbnailOptions;
    private LinearLayout noThumbnailOptions;
    private ImageView imageSet;
    private TextView close;
    private TextView titleTextView;
    private EditText titleEditText;
    private TextView urlTextView;
    private TextView descriptionTextView;
    private EditText descriptionEditText;
    private TextView countTextView;
    private CheckBox noThumbCheckBox;
    private Button previousButton;
    private Button forwardButton;
    private String currentTitle, currentDescription = "";

    public UrlPreviewOld(Context context) {
        super(context);
        init(context);
    }

    public UrlPreviewOld(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public UrlPreviewOld(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setBaselineAligned(false);
        setOrientation(HORIZONTAL);
        inflate(context, R.layout.preview_content, this);

        infoWrap = (LinearLayout) findViewById(R.id.info_wrap);
        titleWrap = (LinearLayout) infoWrap.findViewById(R.id.title_wrap);
        thumbnailOptions = (LinearLayout) findViewById(R.id.thumbnail_options);
        noThumbnailOptions = (LinearLayout) findViewById(R.id.no_thumbnail_options);
        imageSet = (ImageView) findViewById(R.id.image_post_set);
        close = (TextView) titleWrap.findViewById(R.id.close);
        titleTextView = (TextView) titleWrap.findViewById(R.id.title);
        titleEditText = (EditText) titleWrap.findViewById(R.id.input_title);
        urlTextView = (TextView) findViewById(R.id.url);
        descriptionTextView = (TextView) findViewById(R.id.description);
        descriptionEditText = (EditText) findViewById(R.id.input_description);
        countTextView = (TextView) thumbnailOptions.findViewById(R.id.count);
        noThumbCheckBox = (CheckBox) noThumbnailOptions.findViewById(R.id.no_thumbnail_checkbox);
        previousButton = (Button) thumbnailOptions.findViewById(R.id.post_previous);
        forwardButton = (Button) thumbnailOptions.findViewById(R.id.post_forward);
    }

    public void setSourceContent(final SourceContent sourceContent) {
        String imageUrl = sourceContent.getImage();
        imageUrl = imageUrl.startsWith("//") ? "http:" + imageUrl : imageUrl;
        Glide.with(getContext()).load(imageUrl).into(imageSet);

        titleTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                titleTextView.setVisibility(View.GONE);
                titleEditText.setText(TextCrawler.extendedTrim(titleTextView.getText().toString()));
                titleEditText.setVisibility(View.VISIBLE);
            }
        });
        titleEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView arg0,
                                                  int arg1, KeyEvent arg2) {

                        if (arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            titleEditText.setVisibility(View.GONE);

                            currentTitle = TextCrawler
                                    .extendedTrim(titleEditText
                                            .getText().toString());

                            titleTextView.setText(currentTitle);
                            titleTextView.setVisibility(View.VISIBLE);

                        }

                        return false;
                    }
                });
        descriptionTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                descriptionTextView.setVisibility(View.GONE);
                descriptionEditText.setText(TextCrawler.extendedTrim(descriptionTextView.getText().toString()));
                descriptionEditText.setVisibility(View.VISIBLE);
            }
        });
        descriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView arg0,
                                                  int arg1, KeyEvent arg2) {

                        if (arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            descriptionEditText.setVisibility(View.GONE);

                            currentDescription = TextCrawler.extendedTrim(descriptionEditText.getText().toString());

                            descriptionTextView.setText(currentDescription);
                            descriptionTextView.setVisibility(View.VISIBLE);
                        }

                        return false;
                    }
                });

        noThumbCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                        thumbnailOptions.setVisibility(View.VISIBLE);

                        showHideImage(imageSet, infoWrap, !arg1);
                    }
                });

        previousButton.setEnabled(false);

        noThumbnailOptions.setVisibility(View.VISIBLE);

        if (sourceContent.getTitle().equals("")) {
            sourceContent.setTitle(getResources().getString(R.string.enter_title));
        }
        if (sourceContent.getDescription().equals("")) {
            sourceContent.setDescription(getResources().getString(R.string.enter_description));
        }

        titleTextView.setText(sourceContent.getTitle());
        urlTextView.setText(sourceContent.getUrl());
        descriptionTextView.setText(sourceContent.getDescription());

    }

    private void showHideImage(View image, View parent, boolean show) {
        if (show) {
            image.setVisibility(View.VISIBLE);
            parent.setPadding(5, 5, 5, 5);
            parent.setLayoutParams(new LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 2f));
        } else {
            image.setVisibility(View.GONE);
            parent.setPadding(5, 5, 5, 5);
            parent.setLayoutParams(new LayoutParams(0,
                    LayoutParams.WRAP_CONTENT, 3f));
        }
    }

}
