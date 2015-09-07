package huji.ac.il.finderskeepers.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;

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

    /**
     * Uploads an image to parse and returns its ID
     *
     * @param imageFile
     * @return
     */
    public String uploadImage(File imageFile){
        try
        {
            Bitmap bmp = DataSource.normalizeImage(imageFile.getAbsolutePath(), 300, 300,true);
            ParseObject pObj = null;
            ParseFile pFile = null ;
            pObj = new ParseObject (tableName);

            // Ensure bmp has value
            if (bmp == null ) {
                Log.d("Error", "Problem with image");
                return null;
            }

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
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
     * Gets an image from the DB
     *
     * @param id
     * @return
     */
    public Bitmap getImage(String id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(tableName);
        query.whereEqualTo("objectId", id);
        try{
            ParseObject object = query.get(id);
            if (object != null) {
                ParseFile file = (ParseFile)object.get("FileName");
                byte[] data = file.getData();
                Log.d(TAG,"got image from Parse. File id: " + file.getName());
                return BitmapFactory.decodeByteArray(data,0, data.length);
            }
            else {
                return null;
            }
        }
        catch (ParseException e){
            Log.d(TAG,"get image failed");
            return null;
        }


    }
}
