package huji.ac.il.finderskeepers.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Persistent data layer for storing items, with Parse.com as its underlying physical layer.
 *
 * Communication with Parse.com is done using blocking (synchronous) functions and should run
 * in a background thread.
 *
 * When migrating to other database services, a similar class should be created fitting the database.
 *
 * Created by Paz on 8/23/2015.
 */
public class ImageDB {
    private String tableName;
    private static final String TAG = "ImageDB";

    /**
     * Ctor
     *
     * @param tableName
     */
    public ImageDB(String tableName){
        this.tableName = tableName;
    }

    public String uploadImage(File imageFile){
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
            pFile = new ParseFile(imageFile.getName(), stream.toByteArray());
            try
            {

                pFile.save();
                Log.d(TAG, "Uploaded image file. URL: " + pFile.getUrl());
                pObj.put("FileName", pFile);
                pObj.save();
                Log.d(TAG, "Added image file to DB. ID: " + pObj.getObjectId());
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

    /**
     * Decodes an image file and resizes it if needed
     *
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight)
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
