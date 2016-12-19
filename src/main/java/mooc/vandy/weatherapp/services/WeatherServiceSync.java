package mooc.vandy.weatherapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mooc.vandy.weatherapp.aidl.WeatherCall;
import mooc.vandy.weatherapp.aidl.WeatherData;
import mooc.vandy.weatherapp.utils.Utils;

/**
 * Created by PRAVEENKUMAR on 6/3/2015.
 */
public class WeatherServiceSync extends LifeCycleLoggingService {



    public static Intent makeIntent(Context context) {
        return new Intent(context,
                WeatherServiceSync.class);
    }

    public IBinder onBind(Intent intent) {
        return mAcronymCallImpl;
    }

    private final WeatherCall.Stub mAcronymCallImpl =
            new WeatherCall.Stub() {
                /**
                 * Implement the AIDL AcronymCall expandAcronym() method,
                 * which forwards to DownloadUtils getResults() to obtain
                 * the results from the Acronym Web service and then
                 * returns the results back to the Activity.
                 */
                @Override
                public List<WeatherData> getCurrentWeather(String weather)
                        throws RemoteException {

                    // Call the Acronym Web service to get the list of
                    // possible expansions of the designated acronym.
                    final List<WeatherData> WeatherResults =
                            Utils.getResults(weather);

                    if (WeatherResults != null) {
                        Log.d(TAG, ""
                                + WeatherResults.size()
                                + " results for acronym: "
                                + weather);

                        // Return the list of acronym expansions back to the
                        // AcronymActivity.
                        return WeatherResults;
                    } else {
                        // Create a zero-sized acronymResults object to
                        // indicate to the caller that the acronym had no
                        // expansions.
                        return new ArrayList<WeatherData>();
                    }
                }
            };
}
