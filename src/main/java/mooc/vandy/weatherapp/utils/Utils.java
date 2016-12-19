package mooc.vandy.weatherapp.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import mooc.vandy.weatherapp.aidl.WeatherData;
import mooc.vandy.weatherapp.weatherjsonparser.JsonWeather;
import mooc.vandy.weatherapp.weatherjsonparser.WeatherJSONParser;

/**
 * Created by PRAVEENKUMAR on 6/3/2015.
 */
public class Utils {

    private final static String TAG = Utils.class.getCanonicalName();

    private final static String sWeather_Web_Service_URL = "http://api.openweathermap.org/data/2.5/weather?q=";


    public static List<WeatherData> getResults(final String Weather) {
        final List<WeatherData> returnList =
                new ArrayList<WeatherData>();

        List<JsonWeather> jsonWeathers = null;

        try {
            // Append the location to create the full URL.
            final URL url =
                    new URL(sWeather_Web_Service_URL
                            + Weather);

            // Opens a connection to the Acronym Service.
            HttpURLConnection urlConnection =
                    (HttpURLConnection) url.openConnection();

            // Sends the GET request and reads the Json results.
            try (InputStream in =
                         new BufferedInputStream(urlConnection.getInputStream())) {
                // Create the parser.
                final WeatherJSONParser parser =
                        new WeatherJSONParser();

                // Parse the Json results and create JsonAcronym data
                // objects.
                jsonWeathers = parser.parseJsonStream(in);
                Log.i(TAG, " Returned from parsing");
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "Size of json weather is " + jsonWeathers.size());
        if(jsonWeathers != null && jsonWeathers.size()>0) {
            for (JsonWeather jsonWeather : jsonWeathers)
            {
                returnList.add(new WeatherData(jsonWeather.getName(),jsonWeather.getWind().getSpeed(),jsonWeather.getWind().getDeg(),
                                jsonWeather.getMain().getTemp(),jsonWeather.getMain().getHumidity(), jsonWeather.getSys().getSunrise(),jsonWeather.getSys().getSunset()));
            }
        }
        else
        {
            Log.i(TAG, "return list is null");
        }
        return returnList;
    }


    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                0);
    }


    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_SHORT).show();
    }


}
