package huji.ac.il.finderskeepers.db;

import android.os.AsyncTask;
import android.util.Log;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Persistent data layer for storing items, with Parse.com as its underlying physical layer.
 *
 * Communication with Parse.com is done using blocking (synchronous) functions and should run
 * in a background thread.
 *
 * When migrating to other database services, a similar class should be created fitting the database.
 *
 * Created by Paz on 8/7/2015.
 */
public class ItemDB {
    private String tableName;
    private static final String TAG = "ItemDB";
    private static final String MSG_SAVE_FAILED = "The item could not be saved!";

    /**
     * Ctor
     *
      * @param tableName
     */
    public ItemDB(String tableName){
        this.tableName = tableName;
    }

    /**
     * Adds an item to the DB.
     *
     * @param newItem item to be added
     */
    public String addItem(Item newItem){
        final ParseObject itemParseObject = itemToParseObject(newItem);
        try{
            itemParseObject.save();
            String id = itemParseObject.getObjectId();
            Log.d(TAG, "addItem: Item saved to DB. ID: " + id);
            return id;
        }
        catch(ParseException e)
        {

            Log.d(TAG, "addItem: Item couldn't be saved: " + e);
            return null;
        }
    }

    /**
     * Finds and removes an item from the DB.
     *
     * @param item
     */
    public void removeItem(Item item){
        ParseObject po = fetchItemObject(item.getReporterID(), item.getCreationDate());
        if (po != null){
            try{
                String id = po.getObjectId();
                po.delete(); // THIS BLOCKS!
                Log.d(TAG, "removeItem: item removed successfully. ID: " + id);
            }
            catch (ParseException e){
                Log.d(TAG, "removeItem: remove item failed: " + e.getMessage());
            }
        }
    }

    /**
     * Fetches from the DB a ParseObject with attributes matching the given item's.
     * The key used for matching is: (reporter_id, creation_date)
     *
     * @param reporterID
     * @param creationDate
     * @return matching ParseObject
     */
    public ParseObject fetchItemObject(String reporterID, Date creationDate) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("reporter_id", reporterID);
        query.whereEqualTo("creation_date", creationDate);

        try {
            ParseObject result = query.getFirst(); // THIS BLOCKS!
            Log.d(TAG, "fetchItemObject: object found in query. ID: " + result.getObjectId());
            return result;
        }
        catch (ParseException e){
            Log.d(TAG, "fetchItemObject: no object found: " + e.getMessage());
            return null;
        }
    }

    public Item findItemByID(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("objectId", id);
        try {
            ParseObject result = query.get(id); // THIS BLOCKS!
            Log.d(TAG, "fetchItemByID: object found in query. ID: " + result.getObjectId());
            return parseObjectToItem(result);
        }
        catch (ParseException e){
            Log.d(TAG, "fetchItemByID: no object found: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<Item> findItemsTypeConditionDistance (ItemType type, ItemCondition minimalCondition,
                                                      LatLng fromPoint, double distance) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("item");
        query.whereEqualTo("type", type.value);
        query.whereGreaterThanOrEqualTo("condition", minimalCondition.value);
        query.whereWithinKilometers("location", new ParseGeoPoint(fromPoint.latitude, fromPoint.longitude), distance);

        try {
            List<ParseObject> objectList = query.find();
            return parseObjectListToItemList(objectList);
        }
        catch (ParseException e) {
            Log.d(TAG, "Find operation failed: " + e.getMessage());
        }

        return new ArrayList<Item>();
    }

    public List<Item> findItemsInGeoBox (LatLng lowerLeft, LatLng upperRight) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("item");
        query.whereWithinGeoBox("location", new ParseGeoPoint(lowerLeft.latitude, lowerLeft.longitude),
                                            new ParseGeoPoint(upperRight.latitude, upperRight.longitude));

        try {
            List<ParseObject> objectList = query.find();
            return parseObjectListToItemList(objectList);
        }
        catch (ParseException e) {
            Log.d(TAG ,"GeoBox Find operation failed: " + e.getMessage());
        }

        return new ArrayList<Item>();
    }

    /**
     * Converts an Item into a ParseObject
     *
     * @param item item to be converted
     * @return converted ParseObject
     */
    private ParseObject itemToParseObject(Item item){
        ParseObject itemObject = new ParseObject(tableName);
        itemObject.put("condition", item.getCondition().value);
        itemObject.put("description", item.getDescription());
        itemObject.put("creation_date", item.getCreationDate());
        //using the ParseGeoPoint which supports various geo-location methods:
        itemObject.put("location",new ParseGeoPoint(item.getLatitude(),item.getLongitude()));
        itemObject.put("reporter_id", item.getReporterID());
        itemObject.put("type", item.getType().value);
        itemObject.put("image_id", item.getImageID());
        return  itemObject;
    }

    private Item parseObjectToItem (ParseObject parseObject) {
        Item item = new Item(parseObject.getObjectId(),
                         parseObject.getParseGeoPoint("location").getLatitude(),
                         parseObject.getParseGeoPoint("location").getLongitude(),
                         ItemType.fromInt(parseObject.getInt("type")),
                         ItemCondition.fromInt(parseObject.getInt("condition")),
                         parseObject.getString("description"),
                         parseObject.getString("reporter_id"),
                         parseObject.getCreatedAt());
        item.setImageID(parseObject.getString("image_id"));
        return item;
    }

    private ArrayList<Item> parseObjectListToItemList(List <ParseObject> parseObjectList) {
        ArrayList<Item> itemList = new ArrayList<Item>();

        for (ParseObject parseObject : parseObjectList ) {
            itemList.add(parseObjectToItem(parseObject));
        }
        return itemList;
    }

}
