package huji.ac.il.finderskeepers.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;
import huji.ac.il.finderskeepers.data.User;

/**
 * Acts as an "interface" to the various databases used.
 * All of the app's access to the databases should use this "interface".
 *
 * Usage: use getDataSource() to get an instance (singleton) of the DataSource
 *
 * Created by Paz on 8/7/2015.
 */
public class DataSource {
    private final String TAG = "DateSource";
    private ItemDB items;
    private UserDB users;
    private ImageDB images;
    private static DataSource singleton = null;

    /**
     * private ctor
     */
    private DataSource(){
        items = new ItemDB("item");
        users = new UserDB("_User"); //this
        images = new ImageDB("image");
    }

    /**
     * returns an instance of the DataSource
     *
     * @return
     */
    public static DataSource getDataSource(){
        if (singleton == null){
            singleton = new DataSource();
        }
        return singleton;
    }

    /**
     * Adds an item to the DB (synchronously)
     * @param newItem item to be added
     * @return id of item
     */
    public String addItem(Item newItem){ return items.addItem(newItem); }

    /**
     * Finds and removes an item from the DB (synchronously)
     * @param item
     */
    public void removeItem(Item item){ items.removeItem(item); }

    /**
     * Finds items in the database whose type is equal to the asked type and condition is
     * at least minimal condition
     * @param type
     * @param minimalCondition
     * @return items in the database that fit the search criteria
     */

    public ArrayList<Item> findItems(ItemType type, ItemCondition minimalCondition,
                                     LatLng fromPoint, double distance, String description) {
        return items.findItems(type, minimalCondition, fromPoint, distance, description);
    }

    public List<Item> findItemsInGeoBox (LatLng lowerLeft, LatLng upperRight, boolean showOnlyAvailable) {
        return items.findItemsInGeoBox(lowerLeft, upperRight, showOnlyAvailable);
    }

    /**
     * Adds a user to the DB (synchronously).
     * If the user already exists, null is returned.
     *
     * @param username
     */
    public User addUser(String username, LatLng location){ return users.addUser(username, location); }

    /**
     * Adds the itemid to the list of items reported by the user with userid
     *
     * @param userid
     * @param itemid
     */
    public void addToReportedItems(String userid, String itemid){ users.addToReportedItems(userid,itemid);}

    /**
     * Adds the itemid to the list of items collected by the user with userid
     *
     * @param userid
     * @param itemid
     */
    public void addToCollectedItems(String userid, String itemid){ users.addToCollectedItems(userid, itemid);}


        /**
         * Adds an image to the DB
         *
         * @param imageFile
         * @return id of image
         */
    public String uploadImage(File imageFile){
        return images.uploadImage(imageFile);   }

    /**
     * marks an item in the db as unavailable
     *
     * @param item
     */
    public void setUnavailable(Item item){ items.setUnavailable(item); }

    public void setHomeLocation(String userid, LatLng location){users.setHomeLocation(userid,location);}

    public User getReporter (Item item) { return users.getUser(item.getReporterID());}

    /**
     * Returns a user with the given ID.
     *
     * @param id
     * @return
     */
    public User getUser(String id){ return users.getUser(id); }

    /**
     * gets an image from the DB
     *
     * @param id
     * @return
     */
    public Bitmap getImage(String id){
        return images.getImage(id);
    }

    public Item findItemByID(String id){
        return items.findItemByID(id);
    }

    /**
     * Decodes an image file and resizes it if needed
     *
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * Creates, scales and corrects the orientation of an image.
     *
     * @param path
     * @return
     */
    public static Bitmap normalizeImage(String path, int targetWidth, int targetHeight, boolean cropToSquare){
        try{
            //get orientation
            ExifInterface exif = new ExifInterface(path);
            int orientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            //read raw image
            Bitmap bitmap = BitmapFactory.decodeFile(path);

            //resize the image so it takes less memory
            bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true);

            //crop to square at middle if selected
            if (cropToSquare){
                int d = Math.min(bitmap.getWidth(),bitmap.getHeight());
                int marginLeft = (bitmap.getWidth() - d)/2;
                int marginTop = (bitmap.getHeight() - d)/2;
                bitmap = Bitmap.createBitmap(bitmap,marginLeft,marginTop,d,d);
            }

            //resize image
            bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

            //rotate according to orientation:
            Matrix matrix = new Matrix();
            switch (orientation){
                case (ExifInterface.ORIENTATION_ROTATE_90): {
                    matrix.postRotate(90);
                    break;
                }
                case (ExifInterface.ORIENTATION_ROTATE_180):{
                    matrix.postRotate(180);
                    break;
                }
                case (ExifInterface.ORIENTATION_ROTATE_270):{
                    matrix.postRotate(270);
                    break;
                }
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return bitmap;
        }
        catch (Exception e){
            Log.d("image orientation: ", e.getMessage());
            return null;
        }
    }

}
