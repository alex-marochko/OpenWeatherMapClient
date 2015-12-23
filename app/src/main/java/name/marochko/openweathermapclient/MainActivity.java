package name.marochko.openweathermapclient;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int cityListId = -1;
    CurrentWeather cw;

    TextView textViewCity;
    ImageView imageViewWeatherIcon;
    TextView textViewTemperature;
    TextView textViewMaxTemperature;
    TextView textViewMinTemperature;
    TextView textViewWeatherDescription;
    TextView textViewHumidity;
    TextView textViewPressure;
    TextView textViewWindSpeed;

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

        progressBar = (ProgressBar)findViewById(R.id.progressBarDataLoading);

        if(cityListId > -1) {
            progressBar.setVisibility(View.INVISIBLE);
            fillViews();
        }else{
            startSettingsActivity();
        }

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
                progressBar.setVisibility(View.INVISIBLE);
                fillViews();

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

        if(cityListId > -1) {

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
        }

/*
        else{

            textViewCity.setText(TV_DUMMY);

            imageViewWeatherIcon.setImageResource(R.drawable.na);

            textViewTemperature.setText(String.format(getString(R.string.temperature),
                    TV_DUMMY));
            textViewMaxTemperature.setText(String.format(getString(R.string.max_temperature),
                    TV_DUMMY));
            textViewMinTemperature.setText(String.format(getString(R.string.min_temperature),
                    TV_DUMMY));
            textViewWeatherDescription.setText(TV_DUMMY);
            textViewHumidity.setText(String.format(getString(R.string.humidity),
                    TV_DUMMY) + "%");
            textViewPressure.setText(String.format(getString(R.string.pressure),
                    TV_DUMMY));
            textViewWindSpeed.setText(String.format(getString(R.string.wind_speed),
                    TV_DUMMY));

        }
*/

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
        cityListId = preferences.getInt("cityListId", -1);
    }

    @Override
    protected void onResume(){
        super.onResume();

        prepareViews();
    }

}

