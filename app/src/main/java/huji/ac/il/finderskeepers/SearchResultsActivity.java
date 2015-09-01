package huji.ac.il.finderskeepers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import huji.ac.il.finderskeepers.data.Item;


public class SearchResultsActivity extends FragmentActivity{

    GoogleMap searchResultsMap = null;
    ArrayList<Item> searchResults = null;
    LatLng fromPoint = null;
    final int SEARCH_RESULTS_FOCUS_PADDING = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Intent callingIntent = getIntent();
        searchResults = callingIntent.getParcelableArrayListExtra("searchResults");
        fromPoint = callingIntent.getParcelableExtra("fromPoint");
        setUpMapIfNeeded();

        Button updateSearchBtn = (Button) findViewById(R.id.searchResultsUpdateSearchBtn);
        updateSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateSearchClick(v);
            }
        });

        Button finishSearchBtn = (Button) findViewById(R.id.searchResultsFinishSearchBtn);
        finishSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishSearchClick(v);
            }
        });


    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (searchResultsMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            searchResultsMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.searchResultsMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (searchResultsMap != null) {
                searchResultsMap.setMyLocationEnabled(true);
                searchResultsMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromPoint, 14));
                searchResultsMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        zoomOnItems();
                    }
                });
            }
        }
    }



    public void zoomOnItems( ) {
        assert(searchResults.size() > 0);

        double leftBound = fromPoint.longitude;
        double rightBound = fromPoint.longitude;
        double upperBound = fromPoint.latitude;
        double lowerBound = fromPoint.latitude;

        for (Item item : searchResults) {
            if (item.getLatitude() <  lowerBound) { lowerBound = item.getLatitude(); }
            if (item.getLatitude() >  upperBound) { upperBound = item.getLatitude(); }
            if (item.getLongitude() <  leftBound) { leftBound = item.getLongitude(); }
            if (item.getLongitude() >  rightBound) { rightBound = item.getLongitude(); }
        }

        LatLngBounds mapBounds = new LatLngBounds(new LatLng(lowerBound, leftBound),
                                                  new LatLng(upperBound, rightBound));

        try {
            searchResultsMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBounds,SEARCH_RESULTS_FOCUS_PADDING));
        }
        catch (Exception e) {
            Log.d("move camera", e.getMessage());
        }

        for (Item item : searchResults) {
            searchResultsMap.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getLatitude(), item.getLongitude())));

        }
    }

    private void onUpdateSearchClick(View v) { this.finish();}

    private void onFinishSearchClick(View v) {
        Intent a = new Intent(this,MainScreenActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_results, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
            return rootView;
        }
    }
}
