package name.marochko.openweathermapclient;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SelectCityActivity extends AppCompatActivity {

    private final String LOG_TAG = "marinfo";
    private ListView lvCities;
    private ArrayAdapter<CharSequence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        lvCities = (ListView) findViewById(R.id.listViewCities);

        lvCities.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

         adapter = ArrayAdapter.createFromResource(
                this, R.array.citiesNamesArray,
                android.R.layout.simple_list_item_single_choice);

        lvCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent();
                intent.putExtra("cityListId", position);
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        lvCities.setAdapter(adapter);

    }


}
