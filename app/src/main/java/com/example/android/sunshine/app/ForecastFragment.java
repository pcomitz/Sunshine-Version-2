package com.example.android.sunshine.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
//import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

// json stuff
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Created by pcomitz on 8/22/2016
 *  A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] forecastArray = {"Today -Sunny - 88/63",
                "Tomorrow - Foggy - 78/48",
                "Weds - Cloudy - 72/63",
                "Thurs - Asteroids - 75/65",
                "Fri - Hurricane - 88/98",
                "Saturday - Help Trapped in Wx Station - 68/51",
                "Sun - Sunny - 88/68"};

        ArrayList<String> weekForecast =
                new ArrayList<String>(Arrays.asList(forecastArray));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);

        return rootView;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_refresh) {
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("29484");
            //new FetchWeatherTask().execute();
        }

        return true;
    }
        /*
         * AsyncTask for getting weather
         * add string for postal code
         */
       private class FetchWeatherTask extends AsyncTask<String, Void, Void> {

            private String apiKey = "83bf49cdc21618fa02f20fedea847bf9";
            private String baseURL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
            private String format = "json";
            private String units = "metric";
            private int numDays = 7;
            private final String FORECAST_BASE_URL =
                     "http://api.openweathermap.org/data/2.5/forecast/daily?";
            private final String QUERY_PARAM = "q";
            private final String FORMAT_PARAM = "mode";
            private final String UNITS_PARAM = "units";
            private final String DAYS_PARAM = "cnt";
            private final String APPID_PARAM = "APPID";
            private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
            private Uri buildUri = null;

            @Override
            protected Void doInBackground(String... params) {

            /* add gist here
            *  https://gist.github.com/udacityandroid/d6a7bb21904046a91695
            * */
                // These two need to be declared outside the try/catch
                // so that they can be closed in the finally block.
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String forecastJsonStr = null;

                try {
                    // Construct the URL for the OpenWeatherMap query
                    // Possible parameters are avaiable at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast
                    URL url =
                            new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=29485&mode=json&units=metric&cnt=7&appid=83bf49cdc21618fa02f20fedea847bf9");

                    //use the URL builder
                    buildUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
                    .appendQueryParameter(FORMAT_PARAM,format)
                    .appendQueryParameter(UNITS_PARAM,units)
                    .appendQueryParameter(DAYS_PARAM,Integer.toString(numDays))
                    .appendQueryParameter(APPID_PARAM,apiKey).build();

                    URL newUrl = new URL(buildUri.toString());

                    Log.v(LOG_TAG, "Built URI " + buildUri.toString());

                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) newUrl.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    forecastJsonStr = buffer.toString();

                    //log the weather data
                    Log.v(LOG_TAG, forecastJsonStr);

                } catch (IOException e) {
                    Log.e("ForecastFragment", "Error ", e);
                    // If the code didn't successfully get the weather data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("ForecastFragment", "Error closing stream", e);
                        }
                    }
                }
                /* end gist */
                return null;
            }
        } //AsyncTask
    }///~


