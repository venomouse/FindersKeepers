package huji.ac.il.finderskeepers.db;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;

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
    private static String tableName;
    private static final String TAG = "ItemDB";
    private static final String MSG_SAVE_FAILED = "The item could not be saved!";
    private static final double MINIMAL_DISTANCE = 0.001;
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
    public static String addItem(Item newItem){
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
    public static void removeItem(Item item){
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
    public static ParseObject fetchItemObject(String reporterID, Date creationDate) {
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

    /**
     * finds an item by its ID
     *
     * @param id
     * @return
     */
    public static Item findItemByID(String id) {
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

    public static ArrayList<Item> findItems (ItemType type, ItemCondition minimalCondition,
                                                      LatLng fromPoint, double distance, String description, boolean showOnlyAvailable) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("item");
        query.whereEqualTo("type", type.value);
        query.whereGreaterThanOrEqualTo("condition", minimalCondition.value);

        if (showOnlyAvailable){
            query.whereEqualTo("available", true);
        }

        //parse query bug where if distance = 0 it returns all items
        if (distance == 0){
            distance = MINIMAL_DISTANCE;
        }
        query.whereWithinKilometers("location", new ParseGeoPoint(fromPoint.latitude, fromPoint.longitude), distance);

        //TODO: check - there is another check whether the string is empty in some other place
        if (!description.isEmpty()) {
            query.whereContains("description", description);
        }

        try {
            List<ParseObject> objectList = query.find();
            return parseObjectListToItemList(objectList);
        }
        catch (ParseException e) {
            Log.d(TAG, "Find operation failed: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    public static List<Item> findItemsInGeoBox (LatLng lowerLeft, LatLng upperRight, boolean showOnlyAvailable) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("item");
        query.whereWithinGeoBox("location", new ParseGeoPoint(lowerLeft.latitude, lowerLeft.longitude),
                new ParseGeoPoint(upperRight.latitude, upperRight.longitude));
        if (showOnlyAvailable){
            query.whereEqualTo("available", true);
        }

        try {
            List<ParseObject> objectList = query.find();
            return parseObjectListToItemList(objectList);
        }
        catch (ParseException e) {
            Log.d(TAG ,"GeoBox Find operation failed: " + e.getMessage());
        }

        return new ArrayList<>();
    }

    public static void setUnavailable(Item item){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        try {
            ParseObject result = query.get(item.getId()); // THIS BLOCKS!
            Log.d(TAG, "object found in query. ID: " + result.getObjectId());
            result.put("available", false);
            result.save();
        }
        catch (ParseException e){
            Log.d(TAG, "no object found: " + e.getMessage());
        }

    }


    /**
     * Converts an Item into a ParseObject
     *
     * @param item item to be converted
     * @return converted ParseObject
     */
    private static ParseObject itemToParseObject(Item item){
        ParseObject itemObject = new ParseObject(tableName);
        itemObject.put("condition", item.getCondition().value);
        itemObject.put("available", item.isAvailable());
        itemObject.put("description", item.getDescription());
        itemObject.put("creation_date", item.getCreationDate());
        //using the ParseGeoPoint which supports various geo-location methods:
        itemObject.put("location",new ParseGeoPoint(item.getLatitude(),item.getLongitude()));
        itemObject.put("reporter_id", item.getReporterID());
        itemObject.put("type", item.getType().value);
        itemObject.put("image_id", item.getImageID());
        return  itemObject;
    }

    private static Item parseObjectToItem (ParseObject parseObject) {
        Item item = new Item(parseObject.getObjectId(),parseObject.getBoolean("available"),
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

    private static ArrayList<Item> parseObjectListToItemList(List <ParseObject> parseObjectList) {
        ArrayList<Item> itemList = new ArrayList<>();

        for (ParseObject parseObject : parseObjectList ) {
            itemList.add(parseObjectToItem(parseObject));
        }
        return itemList;
    }

}
