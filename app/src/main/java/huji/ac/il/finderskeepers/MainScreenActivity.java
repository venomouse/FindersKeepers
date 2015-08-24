package huji.ac.il.finderskeepers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.location.Criteria;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import  huji.ac.il.finderskeepers.data.*;
import huji.ac.il.finderskeepers.db.DataSource;

public class MainScreenActivity extends FragmentActivity implements OnMarkerClickListener {

    private final double  DEFAULT_LATTITUDE = 31.767050;
    private final double DEFAULT_LONGITUDE = 35.204732;

    private final double EXAMPLE_LATTITUDE = 31.766567;
    private final double EXAMPLE_LONGITUDE = 35.206856;
    private final ItemType EXAMPLE_TYPE = ItemType.BOOKS;
    private final ItemCondition EXAMPLE_CONDITION = ItemCondition.NEEDS_REPAIR;
    private final String EXAMPLE_DESCRIPTION = "example_description";
    private final String EXAMPLE_REPORTERID = "example_user_id";
    private final Date EXAMPLE_DATE =  new Date();

    static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private LocationManager locationManager;

    private HashMap <Marker, Item> markerItemMap;
    private LinkedList<Item> itemsToDisplay;
    LatLng myloc = null;
    private String imageFilePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        markerItemMap = new HashMap<Marker, Item>();
        setUpMapIfNeeded();

        
//      Example of adding a new item and user to (Parse) DB
//        try{
//            AddItemTask addItemTask = new AddItemTask();
//            addItemTask.execute(item);
//            //ds.addItemTask(item);
//            //ds.addUser(user);
//        }
//        catch (ParseException e){
//
//        }
        final Button reportItemBtn = (Button) findViewById(R.id.reportItemBtn);
        reportItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        final Button findItemBtn = (Button) findViewById(R.id.findItemBtn);
        findItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFindClick();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
 //       setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //enables my-location layer on a map, showing the user's location all the time
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(this);

        // zooming to the user's location
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        //TODO save common locations/use home location
        myloc = (location == null) ? new LatLng(DEFAULT_LATTITUDE, DEFAULT_LONGITUDE) :
                new LatLng(location.getLatitude(), location.getLongitude());

        //the zoom is between 2 and 21
        //TODO remove the magic number
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 15));

        fetchItemsToDisplay();

        //put marker for every item in the list
        for (Item item : itemsToDisplay)
        {
            Marker marker =  mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getLatitude(), item.getLongitude())));

            markerItemMap.put(marker, item);


        }
    }

    void fetchItemsToDisplay () {
        itemsToDisplay = new LinkedList<Item>();
        itemsToDisplay.add(new Item(EXAMPLE_LATTITUDE,
                EXAMPLE_LONGITUDE,
                EXAMPLE_TYPE,
                EXAMPLE_CONDITION,
                EXAMPLE_DESCRIPTION,
                EXAMPLE_REPORTERID,
                EXAMPLE_DATE));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Item item = markerItemMap.get(marker);

        Intent viewItemIntent = new Intent(this, ViewItemActivity.class);
        viewItemIntent.putExtra("item", item);
        viewItemIntent.putExtra("myLocation", myloc);

        startActivity(viewItemIntent);

        return true;

    }

    private void dispatchTakePictureIntent() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String filename = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss'.jpg'").format(new Date());
        imageFilePath = Environment.getExternalStorageDirectory()+File.separator + filename;
        File file = new File(imageFilePath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
    }

    private void onFindClick() {
        Intent findItemIntent = new Intent(this,FindItemActivity.class);
        findItemIntent.putExtra("myLocation", myloc);
        startActivity(findItemIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Check that request code matches ours:
        if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE)
        {
//            Get our saved file into a bitmap object:
            Intent intent = new Intent(this,AddItemActivity.class);
            intent.putExtra("filepath",imageFilePath);
            startActivity(intent);

        }
    }
}
