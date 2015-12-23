package name.marochko.openweathermapclient;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;


public class OWMIntentService extends IntentService {

    private final String APIKEY = "f9369dfe8f833ce2ad6b2a04e4786123";
    CurrentWeather cwd = null;

    public OWMIntentService() {
        super("OWMIntentService");
    }


    protected void onHandleIntent(Intent intent) {

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


        Gson gson = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create();

        String json = gson.toJson(cwd);

        Intent weatherDataIntent = new Intent().putExtra("weather", json);
        try {
            pendingIntent.send(OWMIntentService.this, MainActivity.REQUEST_PARAM_WEATHER, weatherDataIntent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("OWMC" , MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putString("cwd", json);
        spEditor.commit();

    }

    public void onDestroy() {
        super.onDestroy();
    }

}
