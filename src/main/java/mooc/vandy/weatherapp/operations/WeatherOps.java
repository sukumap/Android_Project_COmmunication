package mooc.vandy.weatherapp.operations;

import android.view.View;

import mooc.vandy.weatherapp.activity.MainActivity;

/**
 * Created by PRAVEENKUMAR on 6/3/2015.
 */
public interface WeatherOps {

    /* Bind Service */
    public void bindService();

    public void unbindService();

    public void onConfigurationChange(MainActivity activity);

    public void downloadWeatherSync(View v);

    public void downloadWeatherASync(View v);
}
