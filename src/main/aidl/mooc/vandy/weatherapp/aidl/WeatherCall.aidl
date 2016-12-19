// WeatherCall.aidl
package mooc.vandy.weatherapp.aidl;

import mooc.vandy.weatherapp.aidl.WeatherData;

// Declare any non-default types here with import statements

interface WeatherCall {
   /**
    * A two-way (blocking) call that retrieves information about the
    * current weather from the Weather Service web service and returns
    * a list of WeatherData objects containing the results from the
    * Weather Service web service back to the WeatherActivity.
    */
    List<WeatherData> getCurrentWeather(in String Weather);
}