package mooc.vandy.weatherapp.weatherjsonparser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

/**
 * Parses the Json weather data returned from the Weather Services API
 * and returns a List of JsonWeather objects that contain this data.
 */
public class WeatherJSONParser {
    /**
     * Used for logging purposes.
     */
    private final String TAG =
        this.getClass().getCanonicalName();
    private List<JsonWeather> retreivedJsonObject ;


    public WeatherJSONParser()
    {
        retreivedJsonObject = new ArrayList<JsonWeather>();

    }
    /**
     * Parse the @a inputStream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonStream(InputStream inputStream)
        throws IOException {
        // TODO -- you fill in here.
        try (JsonReader reader =
                     new JsonReader(new InputStreamReader(inputStream,
                             "UTF-8"))) {
             Log.d(TAG, "Parsing the results returned as an array");

            // Handle the array returned from the Acronym Service.
            return parseJsonWeatherArray(reader);
        }

    }

    /**
     * Parse a Json stream and convert it into a List of JsonWeather
     * objects.
     */
    public List<JsonWeather> parseJsonWeatherArray(JsonReader reader)
        throws IOException {

        int i =0;
        //List<JsonWeather> parsedList = new ArrayList<~>() ;
        JsonWeather returnedWeather;
        reader.beginObject();
        try {
            // If the acronym wasn't expanded return null;
            Log.i(TAG,"Inside parseJsonWeatherArray");
            if (reader.peek() == JsonToken.END_OBJECT) {

                return null;
            }
            returnedWeather = parseJsonWeather(reader);
            if (returnedWeather != null) {

                retreivedJsonObject.add(returnedWeather);
            }
            // Create a JsonAcronym object for each element in the
            // Json array.
          //  parsedList.add(parseWeathers(reader));

        }
        catch(IOException e)
        {
            Log.e(TAG, " Error in parseJsonWeatherArray");
            e.printStackTrace();

        }
        finally {
            reader.endObject();
        }

        return retreivedJsonObject;
    }

    /**
     * Parse a Json stream and return a JsonWeather object.
     */
    public JsonWeather parseJsonWeather(JsonReader reader) 
        throws IOException {

        boolean validData = false;
        // TODO -- you fill in here.
        Log.i(TAG, " Inside parseJsonweather");
        JsonWeather readWeatherObject = new JsonWeather();
        Log.i(TAG, " Before Begin OBject");
        //reader.beginObject();
        Log.i(TAG, " After Begin OBject");
        try {
            while (reader.hasNext()) {
                String name = reader.nextName();
                Log.i(TAG," String returned is " + name);
                switch (name) {
                    case JsonWeather.id_JSON:
                        reader.skipValue();
                        break;
                    case JsonWeather.dt_JSON:
                        reader.skipValue();
                        break;
                    case JsonWeather.name_JSON:
                        //reader.skipValue();
                        readWeatherObject.setName(reader.nextString());;
                        break;
                    case JsonWeather.cod_JSON:
                        reader.skipValue();
                        break;
                    case JsonWeather.sys_JSON:
                        readWeatherObject.setSys(parseSys(reader));
                        validData = true;
                        break;
                    case JsonWeather.main_JSON:
                        readWeatherObject.setMain(parseMain(reader));
                        validData = true;
                        break;
                    case JsonWeather.wind_JSON:
                        readWeatherObject.setWind(parseWind(reader));
                        validData = true;
                        break;
                    case JsonWeather.weather_JSON:
                        readWeatherObject.setWeather(parseWeathers(reader));;
                        validData = true;
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }

        }
        catch (Exception e) {
            Log.e(TAG, "Crash during parseJsonWeather ");
            e.printStackTrace();

        }
        finally {
           // reader.endObject();

        }
        if (validData == true)
        return readWeatherObject;
        else
            return null;
    }
    
    /**
     * Parse a Json stream and return a List of Weather objects.
     */
    public List<Weather> parseWeathers(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        List<Weather> weatherObjects = new ArrayList<>();
        Log.i(TAG,"Insied parseweather");
        reader.beginArray();
        try {
            while(reader.hasNext()) {
                weatherObjects.add(parseWeather(reader));
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, " Crash during parseWeathers");
            e.printStackTrace();

        }
        finally {
            reader.endArray();
        }
        return weatherObjects;
    }

    /**
     * Parse a Json stream and return a Weather object.
     */
    public Weather parseWeather(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        Weather weatherObject = new Weather();
        reader.beginObject();
        Log.i(TAG,"Inside parseweather");

        try {

            while(reader.hasNext())
            {
                String name = reader.nextName();
                switch (name) {
                    case Weather.id_JSON:
                        weatherObject.setId(reader.nextLong());
                        break;
                    case Weather.description_JSON:
                        weatherObject.setDescription(reader.nextString());
                        break;
                    case Weather.main_JSON:
                        weatherObject.setMain(reader.nextString());
                        break;
                    case Weather.icon_JSON:
                        weatherObject.setIcon(reader.nextString());
                        break;
                    default:
                         reader.skipValue();
                        break;
                }
            }
        }
        catch(IOException e)
        {
            Log.e(TAG,"Crash during parseWeather");
            e.printStackTrace();

        }
        finally {
        reader.endObject();
        }
        return weatherObject;
    }
    
    /**
     * Parse a Json stream and return a Main Object.
     */
    public Main parseMain(JsonReader reader) 
        throws IOException {
        // TODO -- you fill in here.
        Main retreivedMain = new Main();
        reader.beginObject();
        Log.i(TAG,"Inside Parse Main");
        try {
            while(reader.hasNext())
            {
                String name = reader.nextName();
                switch (name)
                {
                    case Main.grndLevel_JSON:
                        retreivedMain.setGrndLevel(reader.nextDouble());
                        break;
                    case Main.humidity_JSON:
                         retreivedMain.setHumidity(reader.nextLong());
                        break;
                    case Main.pressure_JSON:
                         retreivedMain.setPressure(reader.nextDouble());
                        break;
                    case Main.seaLevel_JSON:
                        retreivedMain.setSeaLevel(reader.nextDouble());
                        break;
                    case Main.temp_JSON:
                        retreivedMain.setTemp(reader.nextDouble());
                        break;
                    case Main.tempMax_JSON:
                        retreivedMain.setTempMax(reader.nextDouble());
                        break;
                    case Main.tempMin_JSON:
                        retreivedMain.setTempMin(reader.nextDouble());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        }
        catch (IOException e)
        {
            Log.e(TAG,"Crash during parse Main");
            e.printStackTrace();

        }
        finally {
            reader.endObject();
        }
        return retreivedMain;
    }

    /**
     * Parse a Json stream and return a Wind Object.
     */
    public Wind parseWind(JsonReader reader) throws IOException {
        // TODO -- you fill in here.
        Wind retreivedWind = new Wind();
        reader.beginObject();
        Log.i(TAG," Inside Parse Wind");
        try {
            while(reader.hasNext()) {
                String name = reader.nextName();
                switch(name) {
                    case Wind.deg_JSON:
                        retreivedWind.setDeg(reader.nextDouble());
                        break;
                    case Wind.speed_JSON:
                        retreivedWind.setSpeed(reader.nextDouble());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Crash during wind parsing");
            e.printStackTrace();

        }
        finally {
            reader.endObject();
        }
        return retreivedWind;
    }

    /**
     * Parse a Json stream and return a Sys Object.
     */
    public Sys parseSys(JsonReader reader) throws IOException
    {
        // TODO -- you fill in here.
        Sys retreivedSys = new Sys();
        Log.i(TAG, "Parse Sys");
        reader.beginObject();
        Log.i(TAG, "after begin object");
        try {
            while(reader.hasNext())
            {
                Log.i(TAG, "Next String");
                Log.i(TAG, "Next object is " + reader.peek());

                String name =  reader.nextName();
                Log.i(TAG," Sys String is " +name);

                switch (name)
                {
                    case Sys.country_JSON:
                        retreivedSys.setCountry(reader.nextString());
                        break;

                    case Sys.message_JSON:
                        retreivedSys.setMessage(reader.nextDouble());
                        break;

                    case Sys.sunrise_JSON:
                        retreivedSys.setSunrise(reader.nextLong());
                        break;

                    case Sys.sunset_JSON:
                        retreivedSys.setSunset(reader.nextLong());
                        break;

                    default:
                        reader.skipValue();
                        break;
                }
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, " Crash during sys");
            e.printStackTrace();

        }
        finally
        {
            reader.endObject();
        }
        return retreivedSys;
    }

}
