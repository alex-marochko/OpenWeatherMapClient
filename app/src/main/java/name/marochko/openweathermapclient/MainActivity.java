package name.marochko.openweathermapclient;

import android.app.PendingIntent;
import android.content.Intent;
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
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import net.aksingh.owmjapis.CurrentWeather;
import net.aksingh.owmjapis.OpenWeatherMap;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    int cityListId;

    TextView textViewCity;
    ImageView imageViewWeatherIcon;
    TextView textViewTemperature;
    TextView textViewMaxTemperature;
    TextView textViewMinTemperature;
    TextView textViewWeatherDescription;
    TextView textViewHumidity;
    TextView textViewPressure;
    TextView textViewWindSpeed;

    final String LOG_TAG = "marinfo";
    final static int REQUEST_PARAM_CITIES = 1;
    final static int REQUEST_PARAM_WEATHER = 2;
    CurrentWeather currentWeather;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
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

            Intent intent = new Intent(this, SelectCityActivity.class);
            startActivityForResult(intent, REQUEST_PARAM_CITIES);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data == null) return;

        switch (requestCode) {
            case REQUEST_PARAM_CITIES:
                cityListId = data.getIntExtra("cityListId", 0);
                break;
            case REQUEST_PARAM_WEATHER:
                String weather = data.getStringExtra("weather");
                Gson gson = new Gson();
                currentWeather = gson.fromJson(weather, CurrentWeather.class);

                fillViews(currentWeather);

        }

    }

    public void onClick(View view){

        Log.d(LOG_TAG, "onClick start");

        Intent dummyIntent = new Intent();

        PendingIntent pendingIntent = createPendingResult(REQUEST_PARAM_WEATHER, dummyIntent, 0);

        Intent intent = new Intent(this, OWMIntentService.class);

        intent.putExtra("cityCode", getResources().getIntArray(R.array.citiesCodesArray)[cityListId])
                .putExtra("pendingIntent", pendingIntent);

        startService(intent);


    }

    public void fillViews(CurrentWeather cw){

        textViewCity.setText(getResources().getStringArray(R.array.citiesNamesArray)[cityListId]);

        imageViewWeatherIcon.setImageResource(this.getResources().
                getIdentifier(("i" + cw.getWeatherInstance(0).getWeatherIconName()),
                "drawable", this.getPackageName()));

        textViewTemperature.setText(String.format(getString(R.string.temperature),
                (int) cw.getMainInstance().getTemperature()));
        textViewMaxTemperature.setText(String.format(getString(R.string.max_temperature),
                (int)cw.getMainInstance().getMaxTemperature()));
        textViewMinTemperature.setText(String.format(getString(R.string.min_temperature),
                (int) cw.getMainInstance().getMinTemperature()));
        textViewWeatherDescription.setText(cw.getWeatherInstance(0).getWeatherDescription());
        textViewHumidity.setText(String.format(getString(R.string.humidity),
                (int)cw.getMainInstance().getHumidity())+"%");
        textViewPressure.setText(String.format(getString(R.string.pressure),
                (int)(cw.getMainInstance().getPressure()*0.75006375541921))); //translating from 'hPa' to 'mmHg'
        textViewWindSpeed.setText(String.format(getString(R.string.wind_speed),
                (int)cw.getWindInstance().getWindSpeed()));


    }


}

