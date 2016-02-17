package com.leocardz.link.preview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.Arrays;

/**
 * Created by max on 04.02.16.
 */
public class ActivityListWithLinks extends Activity {

    private ListView listView;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.list);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new ManyLinksAdapter2(this, Arrays.asList(MockData.DATA), listView));
    }

    public void onClickBack(View view) {
        finish();
    }

}
