package mooc.vandy.weatherapp.services;

import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mooc.vandy.weatherapp.aidl.WeatherData;
import mooc.vandy.weatherapp.aidl.WeatherRequest;
import mooc.vandy.weatherapp.aidl.WeatherResults;
import mooc.vandy.weatherapp.utils.Utils;

/**
 * Created by PRAVEENKUMAR on 6/4/2015.
 */
public class WeatherServiceAsync extends LifeCycleLoggingService {

    public static Intent makeIntent(Context context) {
        return new Intent(context,
                WeatherServiceAsync.class);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mAcronymRequestImpl;
    }



    private final WeatherRequest.Stub mAcronymRequestImpl =
            new WeatherRequest.Stub() {
                /**
                 * Implement the AIDL AcronymRequest expandAcronym()
                 * method, which forwards to DownloadUtils getResults() to
                 * obtain the results from the Acronym Web service and
                 * then sends the results back to the Activity via a
                 * callback.
                 */
                @Override
                public void getCurrentWeather(String location,
                                          WeatherResults callback)
                        throws RemoteException {

                    // Call the Acronym Web service to get the list of
                    // possible expansions of the designated acronym.
                    final List<WeatherData> returnedWeatherResults =
                            Utils.getResults(location);

                    if (returnedWeatherResults != null) {
                        Log.d(TAG, ""
                                + returnedWeatherResults.size()
                                + " results for Location: "
                                + location);
                        // Invoke a one-way callback to send list of
                        // acronym expansions back to the AcronymActivity.
                        callback.sendResults(returnedWeatherResults);
                    } else
                        // Send a zero sized array in case of error
                        callback.sendResults(new ArrayList<WeatherData>());
                }
            };
}
