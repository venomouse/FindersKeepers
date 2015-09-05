package huji.ac.il.finderskeepers.data;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paz on 8/8/2015.
 */
public class User implements Serializable{

    private static final double  DEFAULT_LATTITUDE = 31.767050;
    private static final double DEFAULT_LONGITUDE = 35.204732;
    public static final LatLng DEFAULT_LOCATION = new LatLng(DEFAULT_LATTITUDE,DEFAULT_LONGITUDE);

    String id = null; //this is set by the DB
    String username;
    List<String> reportedItemsIDs = new ArrayList<>();
    List<String> collectedItemsIDs = new ArrayList<>();
    LatLng homeLocation = DEFAULT_LOCATION;

    public LatLng getHomeLocation() {
        return homeLocation;
    }

    public void setHomeLocation(LatLng homeLocation) {
        this.homeLocation = homeLocation;
    }



    /**
     * Ctor.
     * id will be set on user creation.
     *
     * @param username
     */
    public User(String username){ this.username = username; }

    public String getUsername(){ return username;}

    public List<String> getReportedItemsIDs() {
        return reportedItemsIDs;
    }

    public void setReportedItemsIDs(List<String> reportedItemsIDs) {
        this.reportedItemsIDs = reportedItemsIDs;
    }

    public List<String> getCollectedItemsIDs() {
        return collectedItemsIDs;
    }

    public void setCollectedItemsIDs(List<String> collectedItemsIDs) {
        this.collectedItemsIDs = collectedItemsIDs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
