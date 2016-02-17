package com.leocardz.link.preview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by max on 11.02.16.
 */
public class Main2 extends Activity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.main2);
    }

    public void onClickNext(View view) {
        Intent intent = new Intent(this, ActivityListWithLinks.class);
        startActivity(intent);
    }

}
