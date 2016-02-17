package com.library.weblinkpreview2.linkPreviewContainerImpl;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by max on 12.02.16.
 */
public abstract class OnAddTextViewListener implements ViewGroup.OnHierarchyChangeListener {

    @Override
    public void onChildViewAdded(View parent, View child) {
        if (child instanceof TextView) {
            onTextViewAdded((TextView) child);
        } else {
            onSomeOneElseAdded(parent, child);
        }
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        if (child instanceof TextView) {
            onTextViewRemoved((TextView) child);
        } else {
            onSomeOneElseRemoved(parent, child);
        }
    }

    protected abstract void onTextViewAdded(TextView textView);

    protected void onTextViewRemoved(TextView textView) {
        //do nothing
    }

    protected void onSomeOneElseAdded(View parent, View child) {
        //do nothing
    }

    protected void onSomeOneElseRemoved(View parent, View child) {
        //do nothing
    }


}
