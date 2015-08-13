package huji.ac.il.finderskeepers.db;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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
     * @param newUser
     */
    public void addUser(User newUser) {
        ParseUser pu = new ParseUser();
        pu.setUsername(newUser.getUsername());

        /**
         * TODO: ParseUser needs to have a password. Do we want to use passwords?
         * In the mean time use default
         */
        pu.setPassword("12345678");
        try {
            pu.signUp();
        } catch (ParseException e) {
            if (e == null) {
                Log.d(TAG, "addUser: User sign-up successful");
            } else {
                Log.d(TAG, "addUser: User sign-up failed: " + e.getMessage());
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
     * @param username
     * @return
     */
    private ParseUser fetchUserObject(String username) {
        ParseQuery<ParseUser> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("username", username);
        try {
            ParseUser result = query.getFirst(); // THIS BLOCKS!
            Log.d(TAG, "fetchUserObject: user found in query. username: " + result.getUsername());
            return result;
        }
        catch (ParseException e){
            Log.d(TAG, "fetchUserObject: no user found: " + e.getMessage());
            return null;
        }
    }
}
