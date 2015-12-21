package name.marochko.openweathermapclient;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;

import java.io.IOException;


public class OWMIntentService extends IntentService {

    private final String LOG_TAG = "marinfo";
    CurrentWeather cwd = null;

    public OWMIntentService() {
        super("OWMIntentService");
        Log.d(LOG_TAG, "OWMIntentService()");
    }


    protected void onHandleIntent(Intent intent) {

        Log.d(LOG_TAG, "onHandleIntent");

        int cityCode = intent.getIntExtra("cityCode", 0);

        // declaring object of "OpenWeatherMap" class
        OpenWeatherMap owm = new OpenWeatherMap("f9369dfe8f833ce2ad6b2a04e4786123");
        // getting current weather data for the "London" city
        try {
            cwd = owm.currentWeatherByCityCode(cityCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(LOG_TAG, cwd.getWindInstance().getWindSpeed() + " " + cwd.getCityName());

    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

}
