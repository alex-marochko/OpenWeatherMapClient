package name.marochko.openweathermapclient;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;

import java.io.IOException;


public class OWMIntentService extends IntentService {

    private final String LOG_TAG = "marinfo";
    private final String APIKEY = "f9369dfe8f833ce2ad6b2a04e4786123";
    CurrentWeather cwd = null;

    public OWMIntentService() {
        super("OWMIntentService");
        Log.d(LOG_TAG, "OWMIntentService()");
    }


    protected void onHandleIntent(Intent intent) {

        Log.d(LOG_TAG, "onHandleIntent");

        PendingIntent pendingIntent = intent.getParcelableExtra("pendingIntent");

        int cityCode = intent.getIntExtra("cityCode", 0);

        // declaring object of "OpenWeatherMap" class (with registered APIKEY)
        OpenWeatherMap owm = new OpenWeatherMap(APIKEY);

        owm.setLang(OpenWeatherMap.Language.RUSSIAN);
        owm.setUnits(OpenWeatherMap.Units.METRIC);


        try {
            cwd = owm.currentWeatherByCityCode(cityCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }


//        Log.d(LOG_TAG, cwd.getMainInstance().getTemperature() + " " + cwd.getCityName());

        Gson gson = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create();
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        gsonBuilder.serializeSpecialFloatingPointValues();

        String json = gson.toJson(cwd);

        Intent weatherDataIntent = new Intent().putExtra("weather", json);
        try {
            pendingIntent.send(OWMIntentService.this, MainActivity.REQUEST_PARAM_WEATHER, weatherDataIntent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }



    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

}
