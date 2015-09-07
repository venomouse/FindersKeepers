package huji.ac.il.finderskeepers.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Maria on 7/25/2015.
 */

public class Item implements Parcelable {

    public static final int  EMPTY_ID = -1;

    public void setId(String id) {
        this.id = id;
    }

    String id = null; //this will be set when item is added to DB

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    boolean available = true;
    double latitude;
    double longitude;

    ItemType type;
    ItemCondition condition;

    String description;
    String reporterID;
    Date creationDate;

    String imageID; // a reference to an image

    /**
     * Ctor for saved items
     *
     * @param id
     * @param available
     * @param latitude
     * @param longitude
     * @param type
     * @param condition
     * @param description
     * @param reporterID
     * @param creationDate
     */
    public Item(String id,boolean available, double latitude, double longitude, ItemType type, ItemCondition condition,
                String description, String reporterID, Date creationDate)
    {
        this.id = id;
        this.available = available;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.condition = condition;
        this.description = description;
        this.reporterID = reporterID;
        this.creationDate = creationDate;
        this.imageID = ""; // at start set to empty string, later get DB-relative ID
    }

    public Item(boolean available, double latitude, double longitude, ItemType type, ItemCondition condition,
                String description, String reporterID, Date creationDate)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.condition = condition;
        this.description = description;
        this.reporterID = reporterID;
        this.creationDate = creationDate;
        this.imageID = ""; // at start set to empty string, later get DB-relative ID
    }

    public Item(Parcel source)
    {
        this.id = source.readString();
        this.available = source.readByte() != 0;
        this.latitude = source.readDouble();
        this.longitude = source.readDouble();
        this.type = ItemType.fromInt(source.readInt());
        this.condition = ItemCondition.fromInt(source.readInt());
        this.description = source.readString();
        this.reporterID = source.readString();
        this.creationDate = (Date) source.readSerializable();
        this.imageID = source.readString();
    }

    public String  getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public ItemType getType() { return type; }
    public ItemCondition getCondition() { return condition; }
    public String getDescription(){return description;}
    public String getReporterID() { return reporterID; }
    public Date getCreationDate()  { return creationDate; }
    public String getImageID() { return imageID; }

    public void setImageID(String id) {imageID = id; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeByte((byte) (available ? 1 : 0));
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(type.value);
        dest.writeInt(condition.value);
        dest.writeString(description);
        dest.writeString(reporterID);
        dest.writeSerializable(creationDate);
        dest.writeString(imageID);
    }

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {

        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
