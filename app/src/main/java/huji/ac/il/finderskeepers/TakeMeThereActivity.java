package huji.ac.il.finderskeepers;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.maps.android.PolyUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.db.DataSource;
import huji.ac.il.finderskeepers.design.RectButton;


/**
 * This activity is used to display the route towards the chosen item
 * it uses Google Directions service to calculate the route, and
 * displays it on the map using the Google Maps API.
 */
public class TakeMeThereActivity extends FragmentActivity {

    GoogleMap directionsMap = null;
    Item item = null;
    String currUserID = null;

    LatLng fromPoint = null;
    LatLng toPoint = null;

    LatLngBounds routeBounds = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_me_there);

        item = getIntent().getParcelableExtra("item");
        fromPoint = getIntent().getParcelableExtra("from");
        toPoint =  getIntent().getParcelableExtra("to");
        setUpMapIfNeeded();

        //setting up the buttons
        RectButton takeMeThereBackBtn = (RectButton) findViewById(R.id.takeMeThereBackBtn);
        takeMeThereBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });

        currUserID = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("userid", null);

        RectButton takeMeTherePickUpBtn = (RectButton) findViewById(R.id.takeMeTherePickUpBtn);
        takeMeTherePickUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickUpClick();
            }
        });

        RectButton takeMeThereGoneBtn = (RectButton) findViewById(R.id.takeMeThereGoneBtn);
        takeMeThereGoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoneClick();
            }
        });

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (directionsMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            directionsMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.takeMeThereMap))
                    .getMap();
            if (directionsMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * Setting up the map - showing the destination and navigation directions
     */
    private void setUpMap() {
        //enables my-location layer on a map, showing the user's location all the time
        directionsMap.setMyLocationEnabled(true);
        directionsMap.addMarker(new MarkerOptions()
                .position(new LatLng(toPoint.latitude, toPoint.longitude))
                .icon(BitmapDescriptorFactory.fromResource(item.getType().markerID)));

        directionsMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromPoint, Common.DEFAULT_CAMERA_ZOOM));
        directionsMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //after we calculated the route, we zoom the map camera
                //so that all the route will be on the map
                if (routeBounds != null) {
                    directionsMap.moveCamera(CameraUpdateFactory.newLatLngBounds(routeBounds,Common.DEFAULT_CAMERA_PADDING));
                }
            }
        });

        new FetchDirectionsTask().execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_me_there, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void onBackClick() {
        this.finish();
    }

    private void onPickUpClick() {
        UpdateItemTask updateItemTask = new UpdateItemTask(currUserID,item);
        updateItemTask.execute(true);
    }

    private void onGoneClick() {
        UpdateItemTask updateItemTask = new UpdateItemTask(currUserID,item);
        updateItemTask.execute(false);
    }

    /**
     *  This task is used to send an HTTP response to the Google Directions service
     *  and use the service response to draw the route on the map.
     */
    private class FetchDirectionsTask extends AsyncTask<URL, Integer, String> {
        List<LatLng> latLngs = new ArrayList<>();
        final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
        final JsonFactory JSON_FACTORY = new JacksonFactory();

        protected String doInBackground(URL... urls) {
            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest request) {
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                }
            });

            String fromString = Double.toString(fromPoint.latitude) + ',' + Double.toString(fromPoint.longitude);
            String toString = Double.toString(toPoint.latitude) + ',' + Double.toString(toPoint.longitude);

            GenericUrl url = new GenericUrl("https://maps.googleapis.com/maps/api/directions/json");
            url.put("origin", fromString);
            url.put("destination", toString);

            url.put("mode","walking");
            url.put("key", getResources().getText(R.string.google_directions_key));

            try {
                HttpRequest request = requestFactory.buildGetRequest(url);
                HttpResponse httpResponse = request.execute();

                DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);
                String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
                latLngs = PolyUtil.decode(encodedPoints);

                LatLng northEast = new LatLng(directionsResult.routes.get(0).bounds.northeast.lat,
                                              directionsResult.routes.get(0).bounds.northeast.lng);

                LatLng southWest = new LatLng(directionsResult.routes.get(0).bounds.southwest.lat,
                        directionsResult.routes.get(0).bounds.southwest.lng);
                routeBounds = new LatLngBounds(southWest, northEast);
            }

            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            //draw the route on the map
            PolylineOptions routeOptions = new PolylineOptions().addAll(latLngs).width(13).color(Color.parseColor("#630263"));
            directionsMap.addPolyline(routeOptions);
        }

    }

    /**-----------------------------Classes used to parse the HTTP Response -------------*/
    public static class DirectionsResult {
        @Key("routes")
        public List<Route> routes;
    }

    public static class Route {
        @Key("bounds")
        public Bounds bounds;
        @Key("overview_polyline")
        public OverviewPolyLine overviewPolyLine;
    }

    public static class OverviewPolyLine {
        @Key("points")
        public String points;
    }

    public static class Bounds {
        @Key("northeast")
        public BoundLatLng northeast;
        @Key("southwest")
        public BoundLatLng southwest;
    }

    public static class BoundLatLng {
        @Key("lat")
        public double lat;
        @Key("lng")
        public double lng;
    }
    /**-------------------------------END classes for parsing---------------------*/


    /**
     * This task updates target item if it was taken or gone
     */
    public class UpdateItemTask extends AsyncTask<Boolean, Integer, Integer> {
        Item item;
        String userid;
        public UpdateItemTask(String currUserID, Item item){
            userid = currUserID;
            this.item = item;
        }

        @Override
        protected void onPreExecute(){
            ProgressBar bar = (ProgressBar) TakeMeThereActivity.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(Boolean... isCollected) {
            DataSource.getDataSource().setUnavailable(item);

            // if item is collected we need to update the user DB
            if (isCollected[0]){
                DataSource.getDataSource().addToCollectedItems(userid, item.getId());
            }
            return 0;
        }

        protected void onPostExecute(Integer status) {
            ProgressBar bar = (ProgressBar) TakeMeThereActivity.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);
            Intent mainIntent = new Intent(TakeMeThereActivity.this, MainScreenActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            mainIntent.putExtra("itemWasRemoved",true);
            startActivity(mainIntent);
        }

    }
}
