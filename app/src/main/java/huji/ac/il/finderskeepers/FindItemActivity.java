package huji.ac.il.finderskeepers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;
import huji.ac.il.finderskeepers.db.DataSource;


public class FindItemActivity extends ActionBarActivity {

    double MAX_DISTANCE = 5;
    final int FIND_BY_CURRENT_LOCATION = 0;
    final int FIND_BY_HOME_LOCATION = 1;

    LatLng fromPoint = null;
    LatLng homePoint = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item);

        final TextView distanceText = (TextView) findViewById(R.id.findItemDistanceText);

        SeekBar distanceSeekBar = (SeekBar) findViewById(R.id.findItemDistanceSeekBar);
        distanceSeekBar.setMax(2*(int)MAX_DISTANCE);
        distanceSeekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        double distance = MAX_DISTANCE * (double) progress / (double) seekBar.getMax();
                        distanceText.setText(distance + " km");
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

        Button findItemFindBtn = (Button) findViewById(R.id.findItemFindBtn);
        findItemFindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFindClick(v);
            }
        });

        boolean isHomeLocationSet = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isHomeLocationSet", false);
        RadioButton homeRadioButton = (RadioButton) findViewById(R.id.findItemDistanceFromHome);
        if (!isHomeLocationSet){
            homeRadioButton.setEnabled(false);
        }
        else {
            homeRadioButton.setEnabled(true);
            float homeLat = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getFloat("homeLocationLat", 0);
            float homeLng = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getFloat("homeLocationLng", 0);
            homePoint = new LatLng(homeLat,homeLng);
        }
    }

    private double distanceKmFromSeekBar(SeekBar seekBar, int progress) {
        return MAX_DISTANCE * (double) progress / (double) seekBar.getMax();
    }

    private void onFindClick(View v) {
        RadioGroup findItemTypeRdg = (RadioGroup) findViewById(R.id.findItemTypeRdg);
        View typeRadioButton = findItemTypeRdg.findViewById(findItemTypeRdg.getCheckedRadioButtonId());
        int typeInt = findItemTypeRdg.indexOfChild(typeRadioButton);

       RatingBar conditionBar = (RatingBar) findViewById(R.id.findItemConditionRatingBar);
       int conditionInt = (int) conditionBar.getRating();

        RadioGroup locationRdg = (RadioGroup) findViewById(R.id.findItemDistanceFromRdg);
        View locationButton = locationRdg.findViewById(locationRdg.getCheckedRadioButtonId());
        int locationInt = locationRdg.indexOfChild(locationButton);
        if (locationInt == FIND_BY_CURRENT_LOCATION){
            fromPoint = (LatLng) getIntent().getParcelableExtra("myLocation");
        }
        else {
            fromPoint = homePoint;
        }

        SeekBar distanceSeekBar = (SeekBar) findViewById(R.id.findItemDistanceSeekBar);
        double distance = distanceKmFromSeekBar(distanceSeekBar, distanceSeekBar.getProgress());

        EditText descriptionEdtText = (android.widget.EditText) findViewById(R.id.findItemDescription);

        FindItemsTask findItemsTask = new FindItemsTask(ItemType.fromInt(typeInt),
                                        ItemCondition.fromInt(conditionInt), fromPoint, distance, descriptionEdtText.getText().toString());

        findItemsTask.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_item, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onSearchResultsReturned(ArrayList<Item> searchResults) {
        if (searchResults.size() != 0) {
            Intent searchResultsIntent = new Intent(this, SearchResultsActivity.class);
            searchResultsIntent.putParcelableArrayListExtra("searchResults", searchResults);
            searchResultsIntent.putExtra("fromPoint", fromPoint);
            startActivity(searchResultsIntent);
        }
        else {
            CharSequence text = "No items found";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            toast.show();
        }

    }


    private class FindItemsTask extends AsyncTask<Void, Integer, ArrayList<Item>> {
        ItemCondition minimalCondition;
        ItemType type;
        LatLng fromPoint;
        double distance;
        String description;

        public FindItemsTask(ItemType type, ItemCondition minimalCondition,
                             LatLng fromPoint, double distance, String description) {
            this.minimalCondition = minimalCondition;
            this.type = type;
            this.fromPoint = fromPoint;
            this.distance = distance;
            this.description = description;
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            DataSource ds = DataSource.getDataSource();
            return ds.findItems(type, minimalCondition, fromPoint, distance,description, true);
        }


        protected void onPostExecute(ArrayList<Item> result) {
            onSearchResultsReturned(result);
        }

    }
}
