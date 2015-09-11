package huji.ac.il.finderskeepers;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.db.DataSource;
import huji.ac.il.finderskeepers.design.RectButton;


public class MainScreenActivity extends FragmentActivity
                                implements OnMarkerClickListener, OnCameraChangeListener {

    private GoogleMap itemsMap;
    private HashMap <Marker, Item> markerItemMap;
    private List<Item> itemsToDisplay;

    LatLng myLoc = null;
    //default location so we're fail-safe
    static LatLng myHomeLoc = new LatLng(31.767050,35.204732);
    Boolean realLocationSet = false;

    private String imageFilePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        Intent intent = getIntent();
        setUpMapIfNeeded();

        if (intent != null && intent.hasExtra("itemWasRemoved")){
            refreshMarkers();
        }


        //setting up the buttons
        final RectButton reportItemBtn = (RectButton) findViewById(R.id.reportItemBtn);
        reportItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReportClick(v);
            }
        });

        final RectButton findItemBtn = (RectButton) findViewById(R.id.findItemBtn);
        findItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFindClick(v);
            }
        });

        final RectButton ProfileBtn = (RectButton) findViewById(R.id.profileBtn);
        ProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onProfileClick(v);
            }
        });
    }

    private void onReportClick(View v) {
        if (!Common.checkInternetConnection(this) || !Common.checkLocationIsOn(this)) {
            return;
        }
        RectButton reportBtn = (RectButton) v;
        reportBtn.setBackgroundResource(R.drawable.main_report_item_btn_rect_pushed);
        dispatchTakePictureIntent();
    }

    /**
     * Activates the camera intent for taking picture of the item
     */
    private void dispatchTakePictureIntent() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        String filename = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss'.jpg'").format(new Date());
        imageFilePath = Environment.getExternalStorageDirectory()+File.separator + filename;
        File file = new File(imageFilePath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, Common.CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
    }

    private void onFindClick(View v) {
        if (!Common.checkInternetConnection(this) ||  !Common.checkLocationIsOn(this) ) {
            return;
        }
        RectButton findBtn = (RectButton) v;
        findBtn.setBackgroundResource(R.drawable.main_find_item_btn_rect_pushed);

        Intent findItemIntent = new Intent(this,FindItemActivity.class);
        findItemIntent.putExtra("myLocation", myLoc);
        startActivity(findItemIntent);

        findBtn.setBackgroundResource(R.drawable.main_find_item_btn_rect);
    }

    private void onProfileClick(View v) {
        if (!Common.checkInternetConnection(this)) {
            return;
        }
        RectButton profileBtn = (RectButton) v;
        profileBtn.setBackgroundResource(R.drawable.main_profile_btn_rect_pushed);

        Intent profileIntent = new Intent(this,ProfileActivity.class);
        profileIntent.putExtra("myLocation", myLoc);
        startActivity(profileIntent);

        profileBtn.setBackgroundResource(R.drawable.main_profile_btn_rect);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #itemsMap} is not null.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (itemsMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            itemsMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (itemsMap != null) {
                setUpMap();
            }
        }

        else {
            onCameraChange(itemsMap.getCameraPosition());
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * This should only be called once and when we are sure that {@link #itemsMap} is not null.
     */
    private void setUpMap() {
        //enables my-location layer on a map, showing the user's location all the time
        itemsMap.setMyLocationEnabled(true);
        itemsMap.setOnMarkerClickListener(this);

        // zooming to the user's location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //this means that the real location (not the saved one) was loaded for the first time
                //we should then zoom in to the user's location on the map
                if (!realLocationSet) {
                    realLocationSet = true;
                    if (location != null) {
                        myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                        itemsMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, Common.DEFAULT_CAMERA_ZOOM));
                    }
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        Location location = locationManager.getLastKnownLocation(provider);

        markerItemMap = new HashMap<>();
        myLoc = (location == null) ? myHomeLoc :
                new LatLng(location.getLatitude(), location.getLongitude());

        itemsMap.setOnCameraChangeListener(this);
        itemsMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, Common.DEFAULT_CAMERA_ZOOM));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Item item = markerItemMap.get(marker);

        Intent viewItemIntent = new Intent(this, ViewItemActivity.class);
        viewItemIntent.putExtra("item", item);
        viewItemIntent.putExtra("myLocation", myLoc);

        startActivity(viewItemIntent);

        return true;

    }

    @Override
    public void onCameraChange(CameraPosition position) {
        refreshMarkers();
    }

    public void refreshMarkers(){
        fetchItemsToDisplay();

        //put marker for every item in the list
        for (Item item : itemsToDisplay) {
            Marker marker = itemsMap.addMarker(new MarkerOptions()
                    .position(new LatLng(item.getLatitude(), item.getLongitude()))
                    .icon(BitmapDescriptorFactory.fromResource(item.getType().markerID)));
            markerItemMap.put(marker, item);
        }
    }

    //load the items that are visible on the map right now
    void fetchItemsToDisplay () {
        LatLng lowerLeft = itemsMap.getProjection().getVisibleRegion().nearLeft;
        LatLng upperRight = itemsMap.getProjection().getVisibleRegion().farRight;

        DataSource ds = DataSource.getDataSource();
        itemsToDisplay = ds.findItemsInGeoBox(lowerLeft, upperRight,true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Check that request code matches ours:
        if (requestCode == Common.CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE)
        {
            //If took picture, get our saved file into a bitmap object:
            if (resultCode == RESULT_OK){
                Intent intent = new Intent(this,AddItemActivity.class);
                intent.putExtra("myLocation", myLoc);
                intent.putExtra("filepath",imageFilePath);
                startActivityForResult(intent, Common.ADD_ITEM_REQUEST_CODE);
            }
            else { //reset appearance
                final RectButton reportItemBtn = (RectButton) findViewById(R.id.reportItemBtn);
                reportItemBtn.setBackgroundResource(R.drawable.main_report_item_btn_rect);
            }
        }

        //returning from add item activity:
        if (requestCode == Common.ADD_ITEM_REQUEST_CODE){
            final RectButton reportItemBtn = (RectButton) findViewById(R.id.reportItemBtn);
            reportItemBtn.setBackgroundResource(R.drawable.main_report_item_btn_rect);
            if (resultCode == RESULT_OK){
                Item item = data.getParcelableExtra("item");
                Marker marker =  itemsMap.addMarker(new MarkerOptions()
                        .position(new LatLng(item.getLatitude(), item.getLongitude()))
                        .icon(BitmapDescriptorFactory.fromResource(item.getType().markerID)));
                markerItemMap.put(marker, item);

                String message = "Item added successfully";
                Toast toast = Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT);
                toast.show();
            }

        }
    }
}
