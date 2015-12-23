package name.marochko.openweathermapclient;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectCityActivity extends AppCompatActivity {


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

        int cityListId = getIntent().getIntExtra("cityListId", -1);

        if( cityListId > -1)
            lvCities.setItemChecked(cityListId, true);
    }
}
