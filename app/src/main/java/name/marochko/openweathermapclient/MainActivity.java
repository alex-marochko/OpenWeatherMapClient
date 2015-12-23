package name.marochko.openweathermapclient;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;


import net.aksingh.owmjapis.CurrentWeather;

public class MainActivity extends AppCompatActivity {

    int cityListId = -1;
    CurrentWeather cw;
    boolean needUpdate = false;

    TextView textViewCity;
    ImageView imageViewWeatherIcon;
    TextView textViewTemperature;
    TextView textViewMaxTemperature;
    TextView textViewMinTemperature;
    TextView textViewWeatherDescription;
    TextView textViewHumidity;
    TextView textViewPressure;
    TextView textViewWindSpeed;
    TextView textViewUpdatedTime;

    ProgressBar progressBar;

    final String LOG_TAG = "marinfo";
    final String TV_DUMMY = "?";
    final static int REQUEST_PARAM_CITIES = 1;
    final static int REQUEST_PARAM_WEATHER = 2;

    final static float HPA_TO_MMHG = 0.75006375541921f;
    CurrentWeather currentWeather;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        restoreData();

    }

    private void prepareViews(){

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewCity = (TextView) findViewById(R.id.textViewCity);
        imageViewWeatherIcon = (ImageView) findViewById(R.id.imageViewWeatherIcon);
        textViewTemperature = (TextView) findViewById(R.id.textViewTemperature);
        textViewMaxTemperature = (TextView) findViewById(R.id.textViewMaxTemperature);
        textViewMinTemperature = (TextView) findViewById(R.id.textViewMinTemperature);
        textViewWeatherDescription = (TextView) findViewById(R.id.textViewWeatherDescription);
        textViewHumidity = (TextView) findViewById(R.id.textViewHumidity);
        textViewPressure = (TextView) findViewById(R.id.textViewPressure);
        textViewWindSpeed = (TextView) findViewById(R.id.textViewWindSpeed);

        textViewUpdatedTime = (TextView) findViewById(R.id.textViewUpdatedTime);

        progressBar = (ProgressBar)findViewById(R.id.progressBarDataLoading);

        if(cityListId > -1) {
            if(needUpdate) {
                loadWeatherData();
                needUpdate = false;
            }else{
                fillViews();
            }
        }else{
            startSettingsActivity();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startSettingsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSettingsActivity(){

        Intent intent = new Intent(this, SelectCityActivity.class);
        intent.putExtra("cityListId", cityListId);
        startActivityForResult(intent, REQUEST_PARAM_CITIES);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) return;

        switch (requestCode) {
            case REQUEST_PARAM_CITIES:
                cityListId = data.getIntExtra("cityListId", -1);

                progressBar.setVisibility(View.VISIBLE);

                loadWeatherData();
                break;
            case REQUEST_PARAM_WEATHER:
                String weather = data.getStringExtra("weather");
                Gson gson = new Gson();
                cw = gson.fromJson(weather, CurrentWeather.class);
                fillViews();
                progressBar.setVisibility(View.GONE);
        }
    }

    public void loadWeatherData(){

        Log.d(LOG_TAG, "onClick start");

        Intent dummyIntent = new Intent();

        PendingIntent pendingIntent = createPendingResult(REQUEST_PARAM_WEATHER, dummyIntent, 0);

        Intent intent = new Intent(this, OWMIntentService.class);

        intent.putExtra("cityCode", getResources().getIntArray(R.array.citiesCodesArray)[cityListId])
                .putExtra("pendingIntent", pendingIntent);

        startService(intent);
    }

    public void fillViews(){

        if(cw != null) {

            if(cw.hasMainInstance()) {

                textViewCity.setText(getResources().getStringArray(R.array.citiesNamesArray)[cityListId]);

                imageViewWeatherIcon.setImageResource(this.getResources().
                        getIdentifier(("i" + cw.getWeatherInstance(0).getWeatherIconName()),
                                "drawable", this.getPackageName()));

                textViewTemperature.setText(String.format(getString(R.string.temperature),
                        (int) cw.getMainInstance().getTemperature()));
                textViewMaxTemperature.setText(String.format(getString(R.string.max_temperature),
                        (int) cw.getMainInstance().getMaxTemperature()));
                textViewMinTemperature.setText(String.format(getString(R.string.min_temperature),
                        (int) cw.getMainInstance().getMinTemperature()));
                textViewWeatherDescription.setText(cw.getWeatherInstance(0).getWeatherDescription());
                textViewHumidity.setText(String.format(getString(R.string.humidity),
                        (int) cw.getMainInstance().getHumidity()) + "%");
                textViewPressure.setText(String.format(getString(R.string.pressure),
                        (int) (cw.getMainInstance().getPressure() * HPA_TO_MMHG))); //converting 'hPa' to 'mmHg'
                textViewWindSpeed.setText(String.format(getString(R.string.wind_speed),
                        (int) cw.getWindInstance().getWindSpeed()));


                Date d = new Date();
                SimpleDateFormat formatting = new SimpleDateFormat("m");
                long l = d.getTime() - cw.getDateTime().getTime();
                String formatted = formatting.format(l) + " мин. назад";
                textViewUpdatedTime.setText(formatted);


            }else {
                textViewCity.setText(R.string.no_connection_message);
            }
        }


    }

    private void saveData(){

        Gson gson = new GsonBuilder()
                .serializeSpecialFloatingPointValues()
                .create();

        String json = gson.toJson(cw);

        SharedPreferences sharedPreferences = getSharedPreferences("OWMC", MODE_PRIVATE);
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        spEditor.putString("cwd", json);
        spEditor.putInt("cityListId", cityListId);
        spEditor.apply();
    }

    @Override
    protected void onPause(){
        super.onPause();
        saveData();
    }

    private void restoreData(){

        SharedPreferences preferences
                = getSharedPreferences("OWMC", MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString;
        jsonString = preferences.getString("cwd", "");
        cw = gson.fromJson(jsonString, CurrentWeather.class);
        cityListId = preferences.getInt("cityListId", - 1);

        if(cw != null){
            if(cw.hasMainInstance()) {

                Date d = new Date();
                //if elapsed time from last data update exceeds 30 min, then set update flag
                if ((d.getTime() - cw.getDateTime().getTime()) > 30 * 60 * 1000) needUpdate = true;
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

        prepareViews();
    }

}

