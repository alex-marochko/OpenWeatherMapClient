package name.marochko.openweathermapclient;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    TextView textView;
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
        textView = (TextView) findViewById(R.id.textView);

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
                textView.setText(cityListId + "");
                break;
            case REQUEST_PARAM_WEATHER:
                String weather = data.getStringExtra("weather");
                Gson gson = new Gson();
                currentWeather = gson.fromJson(weather, CurrentWeather.class);
                textView.setText("We got the information from the Service! " + currentWeather.getWeatherInstance(0).getWeatherCode());
                Log.d(LOG_TAG, "currentWeather.getRawResponse" + currentWeather.getRawResponse());

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

        Log.d(LOG_TAG, "onClick end");

    }


}

