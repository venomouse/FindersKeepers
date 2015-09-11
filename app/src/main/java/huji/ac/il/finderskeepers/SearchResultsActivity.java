package huji.ac.il.finderskeepers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.design.HalfButton;

/**
 * This activity presents the results of the search to the user
 */
public class SearchResultsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener{

    GoogleMap searchResultsMap = null;
    ArrayList<Item> searchResults = null;
    HashMap<Marker, Item> markerItemMap = new HashMap<>();

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

        HalfButton updateSearchBtn = (HalfButton) findViewById(R.id.searchResultsUpdateSearchBtn);
        updateSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateSearchClick(v);
            }
        });

        HalfButton finishSearchBtn = (HalfButton) findViewById(R.id.searchResultsFinishSearchBtn);
        finishSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishSearchClick(v);
            }
        });


    }

    /** Sets up the map for the first time */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (searchResultsMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            searchResultsMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.searchResultsMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (searchResultsMap != null) {
                ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
                bar.setVisibility(View.VISIBLE);
                searchResultsMap.setMyLocationEnabled(true);
                searchResultsMap.setOnMarkerClickListener(this);
                searchResultsMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromPoint, Common.DEFAULT_CAMERA_ZOOM));
                searchResultsMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        zoomOnItems();
                    }
                });
            }
        }
    }

    /**
     * Focuses the map on the geobox that contains all the found items + padding
     */
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
            Marker marker =  searchResultsMap.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getLatitude(), item.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(item.getType().markerID)));
            markerItemMap.put(marker, item);
        }

        ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setVisibility(View.INVISIBLE);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        Item item = markerItemMap.get(marker);

        Intent viewItemIntent = new Intent(this, ViewItemActivity.class);
        viewItemIntent.putExtra("item", item);
        viewItemIntent.putExtra("myLocation", fromPoint);

        startActivity(viewItemIntent);

        return true;

    }
}
