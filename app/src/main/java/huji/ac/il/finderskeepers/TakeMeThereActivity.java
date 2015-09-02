package huji.ac.il.finderskeepers;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.User;
import huji.ac.il.finderskeepers.db.DataSource;


public class TakeMeThereActivity extends FragmentActivity {

    GoogleMap directionsMap = null;
    Item item = null;
    String currUserID = null;
    LatLng fromPoint = null;
    LatLng toPoint = null;

    static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_me_there);

        item = getIntent().getParcelableExtra("item");
        fromPoint = getIntent().getParcelableExtra("from");
        toPoint =  getIntent().getParcelableExtra("to");
        setUpMapIfNeeded();

        Button takeMeThereBackBtn = (Button) findViewById(R.id.takeMeThereBackBtn);
        takeMeThereBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick(v);
            }
        });

        currUserID = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("userid", null);

        Button takeMeTherePickUpBtn = (Button) findViewById(R.id.takeMeTherePickUpBtn);
        takeMeTherePickUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickUpClick(v);
            }
        });

        Button takeMeThereGoneBtn = (Button) findViewById(R.id.takeMeThereGoneBtn);
        takeMeThereGoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGoneClick(v);
            }
        });

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (directionsMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            directionsMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.takeMeThereMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (directionsMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * Setting up the map - showing the
     * This should only be called once and when we are sure that {@link #directionsMap} is not null.
     */
    private void setUpMap() {
        //enables my-location layer on a map, showing the user's location all the time
        directionsMap.setMyLocationEnabled(true);
        Marker marker =  directionsMap.addMarker(new MarkerOptions()
                .position(new LatLng(toPoint.latitude, toPoint.longitude)));

        Marker marker2 =  directionsMap.addMarker(new MarkerOptions()
                .position(new LatLng(fromPoint.latitude, fromPoint.longitude)));
        directionsMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fromPoint, 15));
        new DirectionsFetcher().execute();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_me_there, menu);
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


    private void onBackClick(View v) {
        this.finish();
    }

    private void onPickUpClick(View v) {
        UpdateItemTask updateItemTask = new UpdateItemTask(currUserID,item);
        updateItemTask.execute(true);

//        //TODO add database update
//        Intent a = new Intent(this,MainScreenActivity.class);
//        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(a);
    }
    private void onGoneClick(View v) {
        //TODO add database update
        UpdateItemTask updateItemTask = new UpdateItemTask(currUserID,item);
        updateItemTask.execute(false);

//        Intent a = new Intent(this,MainScreenActivity.class);
//        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(a);
    }



    private class DirectionsFetcher extends AsyncTask<URL, Integer, String> {
        List<LatLng> latLngs = new ArrayList<LatLng>();

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
            //TODO make a constant!
            url.put("mode","walking");
            url.put("key", "AIzaSyBva2JVJ7Y3c5_BmgPQJfFD654AbuutNck");

            try {
                HttpRequest request = requestFactory.buildGetRequest(url);
                HttpResponse httpResponse = request.execute();
                //String response = httpResponse.parseAsString();
                DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);
                String encodedPoints = directionsResult.routes.get(0).overviewPolyLine.points;
                latLngs = PolyUtil.decode(encodedPoints);
            }
            catch (IOException e) {
                e.printStackTrace();

            }

            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
        }

        protected void onPostExecute(String result) {
            directionsMap.addPolyline(new PolylineOptions().addAll(latLngs));
        }

    }

    public static class DirectionsResult {
        @Key("routes")
        public List<Route> routes;
    }

    public static class Route {
        @Key("overview_polyline")
        public OverviewPolyLine overviewPolyLine;
    }

    public static class OverviewPolyLine {
        @Key("points")
        public String points;
    }


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
            startActivity(mainIntent);
        }

    }
}
