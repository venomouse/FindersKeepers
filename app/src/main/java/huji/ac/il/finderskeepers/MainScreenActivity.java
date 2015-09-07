package huji.ac.il.finderskeepers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Criteria;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.system.Os;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import  huji.ac.il.finderskeepers.data.*;
import huji.ac.il.finderskeepers.db.DataSource;
import huji.ac.il.finderskeepers.design.RectButton;
import huji.ac.il.finderskeepers.design.SquareButton;


//Caused by: java.lang.ClassCastException: android.widget.Button cannot be cast to huji.ac.il.finderskeepers.design.SquareButton
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
    private List<Item> itemsToDisplay;
    LatLng myloc = null;
    static LatLng myHomeLoc = null;
    private String imageFilePath = null;
    Boolean realLocationSet = false;

    private static boolean firstRun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("homeLocation")){
            myHomeLoc = (LatLng) intent.getParcelableExtra("homeLocation");
        }


        markerItemMap = new HashMap<Marker, Item>();
        setUpMapIfNeeded();

        final RectButton reportItemBtn = (RectButton) findViewById(R.id.reportItemBtn);
        reportItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RectButton reportBtn = (RectButton) v;
                reportBtn.setBackgroundResource(R.drawable.main_report_item_btn_rect_pushed);

                dispatchTakePictureIntent();
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

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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

        //TODO clean up here!
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //this means that the real location (not the saved one) was loaded for the first time
                //we should then zoom in to the user's location on the map
                if (!realLocationSet) {
                    realLocationSet = true;
                    if (location != null) {
                        myloc = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 15));
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

        //TODO save common locations/use home location
        myloc = (location == null) ? myHomeLoc :
                new LatLng(location.getLatitude(), location.getLongitude());

        //the zoom is between 2 and 21

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                fetchItemsToDisplay();

                //put marker for every item in the list
                for (Item item : itemsToDisplay)
                {
                    Marker marker =  mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(item.getLatitude(), item.getLongitude()))
                            .icon(BitmapDescriptorFactory.fromResource(item.getType().markerID)));

                    markerItemMap.put(marker, item);


                }
            }
        });

        //TODO remove the magic number
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 15));


    }

    void fetchItemsToDisplay () {
        //TODO remove
    //    itemsToDisplay = new LinkedList<Item>();
    //     DataSource ds = DataSource.getDataSource();
    //   Item mySampleItem = ds.findItemByID("J4yHvklOY8");

    //    itemsToDisplay.add(mySampleItem);

        VisibleRegion vr = mMap.getProjection().getVisibleRegion();
        LatLng lowerLeft = mMap.getProjection().getVisibleRegion().nearLeft;
        LatLng upperRight = mMap.getProjection().getVisibleRegion().farRight;

        DataSource ds = DataSource.getDataSource();
        itemsToDisplay = ds.findItemsInGeoBox(lowerLeft, upperRight,true);
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

    private void onFindClick(View v) {
        RectButton findBtn = (RectButton) v;
        findBtn.setBackgroundResource(R.drawable.main_find_item_btn_rect_pushed);

        Intent findItemIntent = new Intent(this,FindItemActivity.class);
        findItemIntent.putExtra("myLocation", myloc);
        startActivity(findItemIntent);

        findBtn.setBackgroundResource(R.drawable.main_find_item_btn_rect);
    }

    private void onProfileClick(View v) {
        RectButton profileBtn = (RectButton) v;
        profileBtn.setBackgroundResource(R.drawable.main_profile_btn_rect_pushed);

        Intent profileIntent = new Intent(this,ProfileActivity.class);
        profileIntent.putExtra("myLocation", myloc);
        startActivity(profileIntent);

        profileBtn.setBackgroundResource(R.drawable.main_profile_btn_rect);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Check that request code matches ours:
        if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE)
        {
//            Get our saved file into a bitmap object:
            Intent intent = new Intent(this,AddItemActivity.class);
            intent.putExtra("myLocation", myloc);
            intent.putExtra("filepath",imageFilePath);
            startActivity(intent);

        }
    }
}
