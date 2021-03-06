package com.example.android.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


// json stuff
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *  Created by pcomitz on 8/22/2016
 *  A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> mForecastAdapter;

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
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    /*
     * Add updateWeather helper method 10/1/2016
     */
    private void updateWeather() {
        FetchWeatherTask  weatherTask = new FetchWeatherTask();
        // from StackOverflow
        // http://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String prefLocation = pref.getString("location", "");

        //udacity approach
        String loc = pref.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        weatherTask.execute(loc);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*
        String[] forecastArray =   {"Today -Sunny - 88/63",
                                    "Tomorrow - Foggy - 78/48",
                                    "Weds - Cloudy - 72/63",
                                    "Thurs - Asteroids - 75/65",
                                    "Fri - Hurricane - 88/98",
                                    "Saturday - Help Trapped in Wx Station - 68/51",
                                    "Sun - Sunny - 88/68"};
        ArrayList<String> weekForecast =
                new ArrayList<String>(Arrays.asList(forecastArray));
        */

        /*
         * The ArrayAdapter will take data from a source and use ot to
         * populate the ListView its attached to
         */
        mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),mForecastAdapter.getItem(position),
                               Toast.LENGTH_SHORT).show();
                // Sunshine Lesson 3
                Intent detailIntent = new Intent(getActivity(),DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, mForecastAdapter.getItem(position));
                startActivity(detailIntent);
                Log.v("PHC", "forecast is:"+mForecastAdapter.getItem(position));
            }
        });
        return rootView;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_refresh) {

            /*
            FetchWeatherTask weatherTask = new FetchWeatherTask();

            // from StackOverflow
            // http://stackoverflow.com/questions/3624280/how-to-use-sharedpreferences-in-android-to-store-fetch-and-edit-values
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String prefLocation = pref.getString("location", "");

            //udacity method
            String loc = pref.getString(getString(R.string.pref_location_key),
                                        getString(R.string.pref_location_default));
            Log.v("PHC","The location is:"+prefLocation);
            Log.v("PHC","The location is:"+loc);
            // weatherTask.execute("29483");
            // weatherTask.execute(prefLocation);
            weatherTask.execute(loc);
            */
            updateWeather();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
        /*
         * AsyncTask for getting weather
         * add string for postal code
         */
       private class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

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

            /**
             * The date/time conversion code is going to be moved outside the asynctask later,
             * so for convenience we're breaking it out into its own method now.
             * see https://gist.github.com/udacityandroid/4ee49df1694da9129af9
             */
            private String getReadableDateString(long time){
                // Because the API returns a unix timestamp (measured in seconds),
                // it must be converted to milliseconds in order to be converted to valid date.
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
                return shortenedDateFormat.format(time);
            }

            /**
             * Prepare the weather high/lows for presentation.
             * see https://gist.github.com/udacityandroid/4ee49df1694da9129af9
             *
             * 11/7 - find purpose - complete what you start
             * Change for Preferences - see Temperature Units Settings
             * https://github.com/udacity/Sunshine-Version-2/compare/3.10_refactor_fetch_weather...3.11_add_units_setting
             *
             */
            private String formatHighLows(double high, double low, String unitType) {

                if(unitType.equals(getString(R.string.pref_units_imperial))) {
                    //convert C to F
                    high = (high * 1.8) + 32;
                    low = (low * 1.8) + 32;
                }
                else if (!unitType.equals(getString(R.string.pref_units_imperial))) {
                    // this is an odd way to handle the metric choice
                    Log.d("LOG_TAG", "Unit type not found:" +unitType);
                }

                // For presentation, assume the user doesn't care about tenths of a degree.
                long roundedHigh = Math.round(high);
                long roundedLow = Math.round(low);
                String highLowStr = roundedHigh + "/" + roundedLow;
                return highLowStr;
            }

            @Override
            protected String[] doInBackground(String... params) {

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
                    // Possible parameters are available at OWM's forecast API page, at
                    // http://openweathermap.org/API#forecast
                    // left in just in case its needed - no longer used TODO: remove this
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
                    Log.v(LOG_TAG,"Forecast string:" + forecastJsonStr);

                } catch (IOException e) {
                    Log.e(LOG_TAG, " Error ", e);
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
                        //From udacity gist
                        //hard to follow - is this the right place ?
                        try {
                            return getWeatherDataFromJson(forecastJsonStr,numDays);
                        } catch(JSONException jse) {
                            Log.e(LOG_TAG,jse.getMessage(),jse);
                        }
                    }
                }
                //// This will only happen if there was an error getting or parsing the forecast.
                return null;
            } //doInBackground

            /*
             * from Udacity gist
             * https://gist.github.com/udacityandroid/4ee49df1694da9129af9
             */

            /**
             * Take the String representing the complete forecast in JSON Format and
             * pull out the data we need to construct the Strings needed for the wireframes.
             *
             * Fortunately parsing is easy:  constructor takes the JSON string and converts it
             * into an Object hierarchy for us.
             */
            private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                    throws JSONException {

                // These are the names of the JSON objects that need to be extracted.
                final String OWM_LIST = "list";
                final String OWM_WEATHER = "weather";
                final String OWM_TEMPERATURE = "temp";
                final String OWM_MAX = "max";
                final String OWM_MIN = "min";
                final String OWM_DESCRIPTION = "main";

                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

                // OWM returns daily forecasts based upon the local time of the city that is being
                // asked for, which means that we need to know the GMT offset to translate this data
                // properly.

                // Since this data is also sent in-order and the first day is always the
                // current day, we're going to take advantage of that to get a nice
                // normalized UTC date for all of our weather.

                Time dayTime = new Time();
                dayTime.setToNow();

                // we start at the day returned by local time. Otherwise this is a mess.
                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

                // now we work exclusively in UTC
                dayTime = new Time();

                String[] resultStrs = new String[numDays];

                // adding preferences 11/7
                // this is where the confusing Udacity diff seems to pick up
                SharedPreferences sharedPrefs =
                        PreferenceManager.getDefaultSharedPreferences(getActivity());
                // get shared pref key/value
                String unitType = sharedPrefs.getString(
                  getString(R.string.pref_units_key)  ,
                  getString(R.string.pref_units_metric)
                );
                //check this
                Log.v(LOG_TAG,"The unitType is:"+unitType);

                for(int i = 0; i < weatherArray.length(); i++) {
                    // For now, using the format "Day, description, hi/low"
                    String day;
                    String description;
                    String highAndLow;

                    // Get the JSON object representing the day
                    JSONObject dayForecast = weatherArray.getJSONObject(i);

                    // The date/time is returned as a long.  We need to convert that
                    // into something human-readable, since most people won't
                    // read "1400356800" as "this saturday".
                    long dateTime;
                    // Cheating to convert this to UTC time, which is what we want anyhow
                    dateTime = dayTime.setJulianDay(julianStartDay+i);
                    day = getReadableDateString(dateTime);

                    // description is in a child array called "weather", which is 1 element long.
                    JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                    description = weatherObject.getString(OWM_DESCRIPTION);

                    // Temperatures are in a child object called "temp".  Try not to name variables
                    // "temp" when working with temperature.  It confuses everybody.
                    JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                    double high = temperatureObject.getDouble(OWM_MAX);
                    double low = temperatureObject.getDouble(OWM_MIN);

                    highAndLow = formatHighLows(high, low, unitType);
                    resultStrs[i] = day + " - " + description + " - " + highAndLow;
                }
                return resultStrs;
            }

            @Override
            /*
             * display the OpenWeather data
             * see https://github.com/udacity/Sunshine-Version-2/compare/2.08_json_parsing...2.09_display_data
             */
            protected void onPostExecute(String[] result) {
                //send results to UI
                if(result != null) {
                    mForecastAdapter.clear();
                    for(String dayForecastString : result) {
                        mForecastAdapter.add(dayForecastString);
                    }
                }
            }

        } //AsyncTask
    }///~


