package huji.ac.il.finderskeepers.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Maria on 7/25/2015.
 */

public class Item implements Serializable {

    public static final int  EMPTY_ID = -1;

    int id;
    double latitude;
    double longitude;

    ItemType type;
    ItemCondition condition;

    int reporterID;
    Date creationDate;

    public Item(int id, double latitude, double longitude, ItemType type, ItemCondition condition,
                int reporterID, Date creationDate)
    {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.condition = condition;
        this.reporterID = reporterID;
        this.creationDate = creationDate;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public ItemType getType() { return type; }
    public ItemCondition getCondition() { return condition; }
    public int getReporterID() { return reporterID; }
    public Date getCreationDate()  { return creationDate; }


}
