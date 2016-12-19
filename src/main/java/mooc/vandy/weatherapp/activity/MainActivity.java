package mooc.vandy.weatherapp.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import mooc.vandy.weatherapp.R;
import mooc.vandy.weatherapp.operations.WeatherOps;
import mooc.vandy.weatherapp.operations.WeatherOpsImpl;
import mooc.vandy.weatherapp.utils.RetainedFragmentManager;


public class MainActivity extends LifeCycleLoggingActivity {

    protected final RetainedFragmentManager mRetainedFragmentManager =
            new RetainedFragmentManager(this.getFragmentManager(),
                    TAG);

    private WeatherOps mWeatherOps;
    private final String WEATHEROPS_OBJ = "WEATHER_OPS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"Entered oncreate");
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        mWeatherOps = new WeatherOpsImpl(this);
        handleConfigurationChanges();
        ;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void handleConfigurationChanges() {

        if (mRetainedFragmentManager.firstTimeIn() ==  true)
        {
            Log.i(TAG, " First time. Create Fragment");
            mWeatherOps = new WeatherOpsImpl(this);
            mRetainedFragmentManager.put(WEATHEROPS_OBJ, mWeatherOps);
            mWeatherOps.bindService();
        }
        else
        {
            mWeatherOps = mRetainedFragmentManager.get(WEATHEROPS_OBJ);
            if (null == mWeatherOps)
            {
                Log.i(TAG,"Null weatherOps. Recreate again");
                mWeatherOps = new WeatherOpsImpl(this);
                mRetainedFragmentManager.put(WEATHEROPS_OBJ,mWeatherOps);
                mWeatherOps.bindService();
            }
            else
            {
                mWeatherOps.onConfigurationChange(this);

            }
        }
    }

    public void syncWeatherDownload(View v)
    {
        mWeatherOps.downloadWeatherSync(v);
        //Intent newIntent = new Intent(MainActivity.this, Weather_Display.class);
        //startActivity(newIntent);
        Log.i(TAG, "Pressed download button");
    }

    public void asyncWeatherDownload(View v)
    {
        mWeatherOps.downloadWeatherASync(v);
        Log.i(TAG, " Pressed Async download method");

    }

    @Override
    protected void onDestroy() {
        // Unbind from the Service.
        mWeatherOps.unbindService();

        // Always call super class for necessary operations when an
        // Activity is destroyed.
        super.onDestroy();
    }
}