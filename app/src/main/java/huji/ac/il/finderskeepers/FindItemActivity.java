package huji.ac.il.finderskeepers;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;
import huji.ac.il.finderskeepers.db.DataSource;


public class FindItemActivity extends ActionBarActivity {

    double MAX_DISTANCE = 5;

    LatLng fromPoint = null;

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
    }

    private double distanceKmFromSeekBar(SeekBar seekBar, int progress) {
        return MAX_DISTANCE * (double) progress / (double) seekBar.getMax();
    }

    private void onFindClick(View v) {
        RadioGroup findItemTypeRdg = (RadioGroup) findViewById(R.id.findItemTypeRdg);
        View typeRadioButton = findItemTypeRdg.findViewById(findItemTypeRdg.getCheckedRadioButtonId());
        int typeInt = findItemTypeRdg.indexOfChild(typeRadioButton);

        RadioGroup findItemConditionRdg = (RadioGroup) findViewById(R.id.findItemConditionRdg);
        View conditionRadioButton = findItemConditionRdg.findViewById(findItemConditionRdg.getCheckedRadioButtonId());
        int conditionInt = findItemConditionRdg.indexOfChild(conditionRadioButton);

        //TODO temporary - need to add other options of fromPoint
        fromPoint = (LatLng) getIntent().getParcelableExtra("myLocation");

        SeekBar distanceSeekBar = (SeekBar) findViewById(R.id.findItemDistanceSeekBar);
        double distance = distanceKmFromSeekBar(distanceSeekBar, distanceSeekBar.getProgress());

        TaskManager.FindItemsTask findItemsTask = new TaskManager.FindItemsTask(this, ItemType.fromInt(typeInt),
                                        ItemCondition.fromInt(conditionInt), fromPoint, distance);

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


    public void complete(ArrayList<Item> searchResults) {
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
}
