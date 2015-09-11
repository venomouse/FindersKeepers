package huji.ac.il.finderskeepers.db;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.User;

/**
 * Persistent data layer for storing users, with Parse.com as its underlying physical layer.
 *
 * Communication with Parse.com is done using blocking (synchronous) functions and should run
 * in a background thread.
 *
 * When migrating to other database services, a similar class should be created fitting the database.
 *
 * Created by Paz on 8/7/2015.
 */
public class UserDB {
    private final String TAG = "UserDB";
    private String tableName;

    /**
     * Ctor
     *
     * @param tableName
     */
    public UserDB(String tableName){
        this.tableName = tableName;
    }

    /**
     * Adds a user to the DB
     *
     * @param username
     */
    public User addUser(String username, LatLng homeLocation) {
        ParseUser pu = new ParseUser();
        pu.put("reportedItems", new JSONArray());
        pu.put("collectedItems", new JSONArray());
        pu.setUsername(username);
        pu.put("homeLocation",new ParseGeoPoint(homeLocation.latitude,homeLocation.longitude));
        /**
         * TODO: ParseUser needs to have a password. Do we want to use passwords?
         * In the mean time use default
         */
        pu.setPassword("12345678");
        try {
            pu.signUp();
            return parseUserToUser(pu);
        } catch (ParseException e) {
            if (e == null) {
                Log.d(TAG, "addUser: User sign-up successful");
                return parseUserToUser(pu);
            } else {
                switch (e.getCode()){
                    case (ParseException.USERNAME_TAKEN):
                        Log.d(TAG, "addUser: User already exists: " + e.getMessage());
                        return null;
                    default:
                        Log.d(TAG, "addUser: User sign-up failed: " + e.getMessage());
                        return null;
                }
            }
        }
    }

    /**
     * Finds and removes a user from the DB.
     *
     * @param user
     */
    public void removeUser(User user){
        ParseUser pu = fetchUserObject(user.getUsername()); // THIS BLOCKS!
        if (pu != null){
            try{
                String username = user.getUsername();
                pu.delete(); // THIS BLOCKS!
                Log.d(TAG, "removeUser: removed user successfully. Username: " + username);
            }
            catch (ParseException e){
                Log.d(TAG, "removeUser: remove user failed: " + e.getMessage());
            }
        }
    }

    /**
     * Fetches from the DB a ParseUser with attributes matching the given user's.
     * The key used for matching is: (username)
     *
     * @param userid
     * @return
     */
    private ParseUser fetchUserObject(String userid) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("objectId", userid);
        try {
            ParseUser result = query.get(userid); //THIS BLOCKS!
            Log.d(TAG, "fetchUserObject: user found in query. username: " + result.getUsername());
            return result;
        }
        catch (ParseException e){
            Log.d(TAG, "fetchUserObject: no user found: " + e.getMessage());
            return null;
        }
    }

    /**
     * Adds an item to the list of the user's reported items.
     *
     * @param userid
     * @param itemid
     */
    public void addToReportedItems(String userid, String itemid){
        ParseUser pu =  fetchUserObject(userid);
        if (pu != null){
            pu.add("reportedItems", itemid);
            try{
                pu.save();
            }
            catch (ParseException e){
                Log.d(TAG, "failed to add item to reported items: " + e.getMessage());
            }

        }
    }

    /**
     * Adds an item to the list of the user's collected items.
     *
     * @param userid
     * @param itemid
     */
    public void addToCollectedItems(String userid, String itemid){
        ParseUser pu =  fetchUserObject(userid);
        if (pu != null){
            pu.add("collectedItems",itemid);
            try{
                pu.save();
            }
            catch (ParseException e){
                Log.d(TAG, "failed to add item to collected items: " + e.getMessage());
            }
        }
    }

    /**
     * gets a user by its ID
     *
     * @param id
     * @return
     */
    public User getUser(String id) {
        ParseUser pu = fetchUserObject(id);
        if (pu == null){
            return null;
        }
        else{
            return parseUserToUser(pu);
        }
    }

    /**
     * Returns a User matching a parse user.
     *
     * @param pu
     * @return
     */
    private User parseUserToUser(ParseUser pu){

        User user = new User(pu.getUsername());

        user.setId(pu.getObjectId());
        List<String> reportedItemsIDs = pu.getList("reportedItems");
        List<String> collectedItemsIDs = pu.getList("collectedItems");
        user.setReportedItemsIDs(reportedItemsIDs);
        user.setCollectedItemsIDs(collectedItemsIDs);
        ParseGeoPoint gp = pu.getParseGeoPoint("homeLocation");
        user.setHomeLocation(new LatLng(gp.getLatitude(),gp.getLongitude()));
        return user;
    }

    /**
     * sets the user's home loacation on the server
     *
     * @param userid
     * @param location
     */
    public void setHomeLocation(String userid, LatLng location){
        ParseUser pu =  fetchUserObject(userid);
        if (pu != null){
            ParseGeoPoint gp = new ParseGeoPoint(location.latitude,location.longitude);
            pu.put("homeLocation", gp);
            try{
                pu.save();
            }
            catch (ParseException e){
                Log.d(TAG, "failed to set home location: " + e.getMessage());
            }
        }
    }
}
