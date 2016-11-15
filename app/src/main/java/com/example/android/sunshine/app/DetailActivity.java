package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * Fix code since udacity course is out of date (wow)
 * https://github.com/udacity/Sunshine-Version-2/blob/3.02_create_detail_activity/app/src/main/java/com/example/android/sunshine/app/DetailActivity.java
 */

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            Log.v("PHC", "Settings menu");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     * Was PlaceHolderFragment
     * Is this the DetailFragment? Did udacity silently change the name ?
     * Assume that it is ....
     * quality of udacity instruction continues to decline
     */
    public static class DetailFragment extends Fragment {

        //Adding Share Intent
        private static final String LOG_TAG = "PHC_"+DetailFragment.class.getSimpleName();
        private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
        private String mForeCastStr;

        public DetailFragment() {
            //Adding Share Intent
            setHasOptionsMenu(true);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            //Sunshine Lesson 3
            Intent intent = getActivity().getIntent();
            // WAS String forecastString = intent.getStringExtra(Intent.EXTRA_TEXT);
            mForeCastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            if((intent != null) && intent.hasExtra(Intent.EXTRA_TEXT)) {
                TextView tv = (TextView) rootView.findViewById(R.id.forecast_textview);
                tv.setText(mForeCastStr);
            }
            return rootView;
        }
    }
}