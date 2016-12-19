package mooc.vandy.weatherapp.operations;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import mooc.vandy.weatherapp.R;
import mooc.vandy.weatherapp.activity.MainActivity;
import mooc.vandy.weatherapp.aidl.WeatherCall;
import mooc.vandy.weatherapp.aidl.WeatherData;
import mooc.vandy.weatherapp.aidl.WeatherRequest;
import mooc.vandy.weatherapp.aidl.WeatherResults;
import mooc.vandy.weatherapp.cache.TimeOutCache;
import mooc.vandy.weatherapp.cache.TimeOutCacheImpl;
import mooc.vandy.weatherapp.services.WeatherServiceAsync;
import mooc.vandy.weatherapp.services.WeatherServiceSync;
import mooc.vandy.weatherapp.utils.GenericServiceConnection;
import mooc.vandy.weatherapp.utils.Utils;

/**
 * Created by PRAVEENKUMAR on 6/3/2015.
 */
public class WeatherOpsImpl implements WeatherOps{

    private  final String TAG = getClass().getSimpleName();
    protected WeakReference<MainActivity> mActivity;
    protected WeakReference<EditText> mEditText;
    protected WeakReference<EditText> mLocationName;
    protected WeakReference<EditText> mWindSpeed;
    protected WeakReference<EditText> mTemperature;
    protected WeakReference<EditText> mHumidity;
    private GenericServiceConnection<WeatherCall> mServiceConnectionSync;
    private GenericServiceConnection<WeatherRequest> mServiceConnectionAsync;
    protected List<WeatherData> mResults;
    private final Handler mDisplayHandler = new Handler();
    private final String NO_DATA = "No Data";
    protected TimeOutCacheImpl<String,WeatherData> weatherHashmapTimeOutCache;
    public String location;


    private final WeatherResults.Stub mAcronymResults =
            new WeatherResults.Stub() {
                /**
                 * This method is invoked by the AcronymServiceAsync to
                 * return the results back to the AcronymActivity.
                 */
                @Override
                public void sendResults(final List<WeatherData> weatherDataList)
                        throws RemoteException {
                    // Since the Android Binder framework dispatches this
                    // method in a background Thread we need to explicitly
                    // post a runnable containing the results to the UI
                    // Thread, where it's displayed.  We use the
                    // mDisplayHandler to avoid a dependency on the
                    // Activity, which may be destroyed in the UI Thread
                    // during a runtime configuration change.
                    mDisplayHandler.post(new Runnable() {
                        public void run() {
                            displayResults(weatherDataList);
                            inserttoCache(weatherDataList);
                        }
                    });
                }
            };



                @Override
    public void bindService() {
        Log.d(TAG, " Bind Service Called");
        if (mServiceConnectionSync.getInterface() == null)
        {
            mActivity.get().getApplicationContext().bindService
                    (WeatherServiceSync.makeIntent(mActivity.get()),
                            mServiceConnectionSync, Context.BIND_AUTO_CREATE);
        }
        if (mServiceConnectionAsync.getInterface() == null)
        {
            mActivity.get().getApplicationContext().bindService
                    (WeatherServiceAsync.makeIntent(mActivity.get()),
                            mServiceConnectionAsync, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void unbindService() {

        Log.d(TAG, "Unbind Service called");
        if (mActivity.get().isChangingConfigurations())
            Log.d(TAG,
                    "just a configuration change - unbindService() not called");
        else {
            Log.d(TAG,
                    "calling unbindService()");

            // Unbind the Async Service if it is connected.
            if (mServiceConnectionSync.getInterface() != null)
                mActivity.get().getApplicationContext().unbindService
                        (mServiceConnectionSync);

            if (mServiceConnectionAsync.getInterface() != null)
                mActivity.get().getApplicationContext().unbindService
                        (mServiceConnectionAsync);

        }
    }



    public void downloadWeatherSync(View v) {
        final WeatherCall weatherCall =
                mServiceConnectionSync.getInterface();
        location =
                mEditText.get().getText().toString();
        WeatherData tempData = (WeatherData)weatherHashmapTimeOutCache.get(location);
        if (tempData != null)
        {
            Log.i(TAG," Displaying from cache");
            displayResult(tempData);
        }
         else
        {
            if (weatherCall != null) {
                // Get the acronym entered by the user.


                resetDisplay();
                Utils.showToast(mActivity.get().getApplicationContext(), "Downloading data for" + location);

                // Use an anonymous AsyncTask to download the Acronym data
                // in a separate thread and then display any results in
                // the UI thread.
                new AsyncTask<String, Void, List<WeatherData>>() {
                    /**
                     * Acronym we're trying to expand.
                     */
                    private String mWeatherCity;

                    /**
                     * Retrieve the expanded acronym results via a
                     * synchronous two-way method call, which runs in a
                     * background thread to avoid blocking the UI thread.
                     */
                    protected List<WeatherData> doInBackground(String... acronyms) {
                        try {
                            mWeatherCity = acronyms[0];
                            return weatherCall.getCurrentWeather(mWeatherCity);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    /**
                     * Display the results in the UI Thread.
                     */
                    protected void onPostExecute(List<WeatherData> weatherDataList) {
                        if (weatherDataList.size() > 0) {
                            displayResults(weatherDataList);
                            inserttoCache(weatherDataList);
                        } else
                            Utils.showToast(mActivity.get(),
                                    "no expansions for "
                                            + mWeatherCity
                                            + " found");
                    }
                    // Execute the AsyncTask to expand the acronym without
                    // blocking the caller.
                }.execute(location);
            }
            else {
                Log.d(TAG, "mAcronymCall was null.");
            }
        }
    }

    public void downloadWeatherASync(View v) {
        final WeatherRequest weatherRequest =
                mServiceConnectionAsync.getInterface();

        WeatherData tempData;
        location =
                mEditText.get().getText().toString();
        tempData = (WeatherData)weatherHashmapTimeOutCache.get(location);
        if (tempData != null)
        {
            Log.i(TAG," Displaying from cache");
            displayResult(tempData);
        }
        else {


            if (weatherRequest != null) {
                // Get the acronym entered by the user.


                resetDisplay();
                Utils.showToast(mActivity.get().getApplicationContext(), "Downloading data for" + location);
                try {
                    // Invoke a one-way AIDL call, which does not block
                    // the client.  The results are returned via the
                    // sendResults() method of the mAcronymResults
                    // callback object, which runs in a Thread from the
                    // Thread pool managed by the Binder framework.
                    weatherRequest.getCurrentWeather(location,
                            mAcronymResults);
                } catch (RemoteException e) {
                    Log.e(TAG,
                            "RemoteException:"
                                    + e.getMessage());
                }
            } else {
                Log.d(TAG,
                        "acronymRequest was null.");
            }
        }
    }


    @Override
    public void onConfigurationChange(MainActivity activity) {
        Log.d(TAG, "OnConfiguration Change called");
        mActivity = new WeakReference<MainActivity>(activity);
        initializeViewFields();
    }

    public WeatherOpsImpl(MainActivity activity)
    {
        Log.d(TAG," In constructor WeatherOpsImpl");
        mActivity = new WeakReference<MainActivity>(activity);
        initializeViewFields();
        initializeNonViewFields();
    }

    private void initializeViewFields()
    {
        Log.d(TAG,"Initialize View Fields");
        mActivity.get().setContentView(R.layout.activity_main);
        mEditText = new WeakReference<>((EditText)mActivity.get().findViewById(R.id.weatherText));
        mWindSpeed = new WeakReference<>((EditText)mActivity.get().findViewById(R.id.WindSpeedDisplay));
        mTemperature = new WeakReference<>((EditText)mActivity.get().findViewById(R.id.TemperatureDisplay));
        mHumidity = new WeakReference<>((EditText)mActivity.get().findViewById(R.id.HumidityDisplay));
        mLocationName= new WeakReference<>((EditText)mActivity.get().findViewById(R.id.LocationNameDisplay));
    }

    private void initializeNonViewFields() {
        mServiceConnectionSync =
                new GenericServiceConnection<WeatherCall>
                        (WeatherCall.class);

        mServiceConnectionAsync =
                new GenericServiceConnection<WeatherRequest>
                        (WeatherRequest.class);

        weatherHashmapTimeOutCache = new TimeOutCacheImpl<>(30);
    }





    private void resetDisplay() {
        Utils.hideKeyboard(mActivity.get(),
                mEditText.get().getWindowToken());
        mHumidity.get().setText(NO_DATA);
        mTemperature.get().setText(NO_DATA);
        mWindSpeed.get().setText(NO_DATA);
        mLocationName.get().setText(NO_DATA);

    }

    private void displayResults(List<WeatherData> results) {
        mResults = results;

        if (mResults != null && mResults.size() >0 ) {
            for (WeatherData result : mResults) {

                displayResult(result);
                weatherHashmapTimeOutCache.put(location, result);
                break;
            }
        }
        else
        {
            // Set blank fields
            Utils.showToast(mActivity.get(),
                    "No Data for "
                            + mEditText.get().getText().toString()
                            + " found");
            resetDisplay();
        }
        // Set/change data set.
        //mAdapter.get().clear();
        //mAdapter.get().addAll(mResults);
        //mAdapter.get().notifyDataSetChanged();
    }

    private void displayResult(WeatherData result)
    {
        Log.i(TAG, "Inside Display Result");
        Log.i(TAG, result.toString());
        mLocationName.get().setText(result.mName);
        mTemperature.get().setText(String.valueOf((int)( 9/5*(result.mTemp - 273) + 32)));
        mWindSpeed.get().setText(String.valueOf(result.mSpeed));
        mHumidity.get().setText(String.valueOf(result.mHumidity));

    }

    private void inserttoCache(List<WeatherData> results) {
        mResults = results;

        if (mResults != null && mResults.size() >0 ) {
            for (WeatherData result : mResults) {
                Log.i(TAG, result.toString());
                Log.i(TAG," Inserting to cache");
                weatherHashmapTimeOutCache.put(location, result);
                break;
            }
        }
        else
        {
            // Set blank fields

        }
        // Set/change data set.
        //mAdapter.get().clear();
        //mAdapter.get().addAll(mResults);
        //mAdapter.get().notifyDataSetChanged();
    }
}
