package huji.ac.il.finderskeepers.db;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.User;

/**
 * Acts as an "interface" to the various databases used.
 * All of the app's access to the databases should use this "interface".
 *
 * Created by Paz on 8/7/2015.
 */
public class DataSource {
    private final String TAG = "DateSource";
    private ItemDB items;
    private UserDB users;
    private ImageDB images;

    public DataSource(){
        items = new ItemDB("item");
        users = new UserDB("user");
        images = new ImageDB("image");
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
    public String uploadImage(File imageFile){ return images.uploadImage(imageFile);
    }

}
