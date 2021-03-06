// WeatherResults.aidl
package mooc.vandy.weatherapp.aidl;

import mooc.vandy.weatherapp.aidl.WeatherData;
import java.util.List;
// Declare any non-default types here with import statements

/**
 * Interface defining the method that receives callbacks from the
 * WeatherServiceAsync.  This method should be implemented by the
 * WeatherActivity.
 */


interface WeatherResults {
    /**
     * This one-way (non-blocking) method allows WeatherServiceAsync
     * to return the List of WeatherData results associated with a
     * one-way WeatherRequest.getCurrentWeather() call.
     */
    oneway void sendResults(in List<WeatherData> results);
}
