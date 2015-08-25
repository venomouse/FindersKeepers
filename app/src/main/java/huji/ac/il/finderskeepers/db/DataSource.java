package huji.ac.il.finderskeepers.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
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
        users = new UserDB("user");
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
    public List<Item> findItemsByTypeConditionDistance(ItemType type, ItemCondition minimalCondition,
                                                       LatLng fromPoint, double distance) {
        return items.findItemsTypeConditionDistance(type, minimalCondition, fromPoint, distance);
    }

    /**
     * Adds a user to the DB (synchronously)
     * @param user
     */
    public void addUser(User user){ users.addUser(user); }

    /**
     * Adds an image to the DB
     *
     * @param imageFile
     * @return id of image
     */
    public String uploadImage(File imageFile){ return images.uploadImage(imageFile);   }

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
}
