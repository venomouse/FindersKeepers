package huji.ac.il.finderskeepers.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.User;

/**
 * Created by Paz on 8/7/2015.
 * Acts as an "interface" to the various databases used.
 * All of the app's access to the databases should use this "interface".
 */
public class DataSource {
    private final String TAG = "DateSource";
    private ItemDB items;
    private UserDB users;

    public DataSource(){
        items = new ItemDB("item");
        users = new UserDB("user");
    }

    /**
     * Adds an item to the DB (asynchronously)
     *
     * @param newItem item to be added
     */
    public String addItem(Item newItem){
        return items.addItem(newItem);
    }

    /**
     * Finds and removes an item from the DB (asynchronously)
     *
     * @param item
     */
    public void removeItem(Item item){
        items.removeItem(item);
    }

    /**
     * Adds a user to the DB (asynchronously)
     *
     * @param user
     */
    public void addUser(User user){
        users.addUser(user);
    }

    public String uploadImageToParse(File imageFile){
        try
        {
            Bitmap bmp = decodeSampledBitmapFromFile(imageFile.getAbsolutePath(), 1000, 700);
            ParseObject pObj = null;
            ParseFile pFile = null ;
            pObj = new ParseObject ("Document");
            pObj.put("Notes", "Some Value");

            // Ensure bmp has value
            if (bmp == null ) {
                Log.d("Error", "Problem with image");
                return null;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            pFile = new ParseFile("new2.jpg", stream.toByteArray());
            try
            {
                pFile.save();
                pObj.put("FileName", pFile);
                pObj.save();
                return pObj.getObjectId();
            }
            catch (com.parse.ParseException e)
            {
                // TODO Auto-generated catch block
                Log.d(TAG,"Error uploading image: " + e.getMessage());
                return null;
            }

        }
        catch (Exception e)
        {
            Log.e("up", "ERROR:" + e.toString());
            return null;
        }
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
    { // BEST QUALITY MATCH

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
