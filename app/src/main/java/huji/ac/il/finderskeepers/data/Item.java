package huji.ac.il.finderskeepers.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Maria on 7/25/2015.
 */

public class Item implements Serializable {

    public static final int  EMPTY_ID = -1;

    String id = null; //this will be set when item is added to DB
    double latitude;
    double longitude;

    ItemType type;
    ItemCondition condition;

    String reporterID;
    Date creationDate;

    String imageID; // a reference to an image

    public Item(double latitude, double longitude, ItemType type, ItemCondition condition,
                String reporterID, Date creationDate)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.condition = condition;
        this.reporterID = reporterID;
        this.creationDate = creationDate;
        this.imageID = ""; // at start set to empty string, later get DB-relative ID
    }

    public String  getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public ItemType getType() { return type; }
    public ItemCondition getCondition() { return condition; }
    public String getReporterID() { return reporterID; }
    public Date getCreationDate()  { return creationDate; }
    public String getImageID() { return imageID; }

}
