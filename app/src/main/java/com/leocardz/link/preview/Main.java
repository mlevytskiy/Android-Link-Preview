package com.leocardz.link.preview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.library.weblinkpreview2.LinkPreviewCallback;
import com.library.weblinkpreview2.SourceContent;
import com.library.weblinkpreview2.TextCrawler;

import java.util.List;
import java.util.Random;


@SuppressWarnings("unused")
public class Main extends ActionBarActivity {

    private EditText editText, editTextTitlePost, editTextDescriptionPost;
    private Button submitButton, postButton, randomButton;

    private Context context;

    private TextCrawler textCrawler;
    private ViewGroup dropPreview, dropPost;

    private TextView previewAreaTitle, postAreaTitle;

    private String currentTitle, currentUrl, currentCannonicalUrl,
            currentDescription;

    private Bitmap[] currentImageSet;
    private Bitmap currentImage;
    private int currentItem = 0;
    private int countBigImages = 0;
    private boolean noThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.main);

        editText = (EditText) findViewById(R.id.input);
        editTextTitlePost = null;
        editTextDescriptionPost = null;

        /** --- From ShareVia Intent */
        if (getIntent().getExtras() != null) {
            String shareVia = (String) getIntent().getExtras().get(Intent.EXTRA_TEXT);
            if (shareVia != null) {
                editText.setText(shareVia);
            }
        }
        if (getIntent().getAction() == Intent.ACTION_VIEW) {
            Uri data = getIntent().getData();
            String scheme = data.getScheme();
            String host = data.getHost();
            List<String> params = data.getPathSegments();
            String builded = scheme + "://" + host + "/";

            for (String string : params) {
                builded += string + "/";
            }

            if (data.getQuery() != null && !data.getQuery().equals("")) {
                builded = builded.substring(0, builded.length() - 1);
                builded += "?" + data.getQuery();
            }

            System.out.println(builded);

            editText.setText(builded);

        }
        /** --- */

        submitButton = (Button) findViewById(R.id.action_go);
        randomButton = (Button) findViewById(R.id.random);
        postButton = (Button) findViewById(R.id.post);

        previewAreaTitle = (TextView) findViewById(R.id.preview_area);
        postAreaTitle = (TextView) findViewById(R.id.post_area);

        /** Where the previews will be dropped */
        dropPreview = (ViewGroup) findViewById(R.id.drop_preview);

        /** Where the previews will be dropped */
        dropPost = (ViewGroup) findViewById(R.id.drop_post);

        textCrawler = new TextCrawler();

        initSubmitButton();
        initRandomButton();

    }

    /**
     * Adding listener to the random button
     */
    private void initRandomButton() {
        randomButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                editText.setText("");
                editText.setText(Main.this.getRandomUrl());
            }
        });
    }

    /**
     * Adding listener to the button
     */
    public void initSubmitButton() {
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                textCrawler.makePreview(callback, editText.getText().toString());
                // , TextCrawler.NONE);
            }
        });
    }

    /** Callback to update your view. Totally customizable. */
    /** onPre() will be called before the crawling. onPos() after. */
    /**
     * You can customize this to update your view
     */
    private LinkPreviewCallback callback = new LinkPreviewCallback() {
        /**
         * This view is used to be updated or added in the layout after getting
         * the result
         */
        private View mainView;
        private LinearLayout linearLayout;
        private View loading;
        private ImageView imageView;

        @Override
        public void onPre() {
            hideSoftKeyboard();

            currentImageSet = null;
            currentItem = 0;

            postButton.setVisibility(View.GONE);
            previewAreaTitle.setVisibility(View.VISIBLE);

            currentImage = null;
            noThumb = false;
            currentTitle = currentDescription = currentUrl = currentCannonicalUrl = "";

            submitButton.setEnabled(false);

            /** Inflating the preview layout */
            mainView = getLayoutInflater().inflate(R.layout.main_view, null);

            linearLayout = (LinearLayout) mainView.findViewById(R.id.external);

            /**
             * Inflating a loading layout into Main View LinearLayout
             */
            loading = getLayoutInflater().inflate(R.layout.loading,
                    linearLayout);

            dropPreview.addView(mainView);
        }

        @Override
        public void onPos(final SourceContent sourceContent, boolean isNull) {

            /** Removing the loading layout */
            linearLayout.removeAllViews();

            if (isNull || sourceContent.getFinalUrl().equals("")) {
                /**
                 * Inflating the content layout into Main View LinearLayout
                 */
                View failed = getLayoutInflater().inflate(R.layout.failed,
                        linearLayout);

                TextView titleTextView = (TextView) failed
                        .findViewById(R.id.text);
                titleTextView.setText(getString(R.string.failed_preview) + "\n"
                        + sourceContent.getFinalUrl());

                failed.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        releasePreviewArea();
                    }
                });

            } else {
                postButton.setVisibility(View.VISIBLE);

                currentImageSet = null;//new Bitmap[sourceContent.getImages().size()];

                /**
                 * Inflating the content layout into Main View LinearLayout
                 */
                final UrlPreviewOld content = new UrlPreviewOld(Main.this);
                linearLayout.addView(content);
                content.setSourceContent(sourceContent);
            }
        }
    };

        /**
         * Change the current image in image set
         */
        private void changeImage(Button previousButton, Button forwardButton,
                                 final int index, SourceContent sourceContent,
                                 TextView countTextView, ImageView imageSet, String url,
                                 final int current) {

            if (currentImageSet[index] != null) {
                currentImage = currentImageSet[index];
                imageSet.setImageBitmap(currentImage);
            } else {
                UrlImageViewHelper.setUrlDrawable(imageSet, url,
                        new UrlImageViewCallback() {

                            @Override
                            public void onLoaded(ImageView imageView,
                                                 Bitmap loadedBitmap, String url,
                                                 boolean loadedFromCache) {
                                if (loadedBitmap != null) {
                                    currentImage = loadedBitmap;
                                    currentImageSet[index] = loadedBitmap;
                                }
                            }
                        });

            }

            currentItem = index;

            if (index == 0)
                previousButton.setEnabled(false);
            else
                previousButton.setEnabled(true);

            if (index == 0)
                forwardButton.setEnabled(false);
            else
                forwardButton.setEnabled(true);

            countTextView.setText((index + 1) + " " + getString(R.string.of) + " "
                    + 0);
        }

        /**
         * Hide keyboard
         */
        private void hideSoftKeyboard() {
            hideSoftKeyboard(editText);

            if (editTextTitlePost != null)
                hideSoftKeyboard(editTextTitlePost);
            if (editTextDescriptionPost != null)
                hideSoftKeyboard(editTextDescriptionPost);
        }

        private void hideSoftKeyboard(EditText editText) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager
                    .hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

        /**
         * Just a set of urls
         */
        private final String[] RANDOM_URLS = {
                "https://www.youtube.com/",
                "http://kasa.in.ua/",
                "http://vnexpress.net/ ",
                "http://facebook.com/ ",
                "http://gmail.com",
                "http://goo.gl/jKCPgp",
                "http://www3.nhk.or.jp/",
                "http://habrahabr.ru",
                "http://www.youtube.com/watch?v=cv2mjAgFTaI",
                "http://vimeo.com/67992157",
                "https://lh6.googleusercontent.com/-aDALitrkRFw/UfQEmWPMQnI/AAAAAAAFOlQ/mDh1l4ej15k/w337-h697-no/db1969caa4ecb88ef727dbad05d5b5b3.jpg",
                "http://www.nasa.gov/", "http://twitter.com",
                "http://bit.ly/14SD1eR"
        };

        /**
         * Returns a random url
         */
        public String getRandomUrl() {
            int random = new Random().nextInt(RANDOM_URLS.length);
            return RANDOM_URLS[random];
        }

        private void releasePreviewArea() {
            submitButton.setEnabled(true);
            postButton.setVisibility(View.GONE);
            previewAreaTitle.setVisibility(View.GONE);
            dropPreview.removeAllViews();
        }
}