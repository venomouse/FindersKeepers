package huji.ac.il.finderskeepers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;
import huji.ac.il.finderskeepers.data.User;
import huji.ac.il.finderskeepers.db.DataSource;

public class AddItemActivity extends ActionBarActivity {
    private final double  DEFAULT_LATTITUDE = 31.767050;
    private final double DEFAULT_LONGITUDE = 35.204732;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        final String path = getIntent().getStringExtra("filepath");
        final Bitmap bitmap;

        bar = (ProgressBar) this.findViewById(R.id.progressBar);

        Button btnAddItem = (Button) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskManager.AddItemTask uploadTask = TaskManager.createAddItemTask(AddItemActivity.this,bar, new File(path));
                //get input fields:
                RadioGroup rdgType = (RadioGroup) findViewById(R.id.rdgType);
                View radioButton = rdgType.findViewById(rdgType.getCheckedRadioButtonId());
                int typeInt = rdgType.indexOfChild(radioButton);
                RadioGroup rdgCondition = (RadioGroup) findViewById(R.id.rdgCondition);
                radioButton = rdgCondition.findViewById(rdgCondition.getCheckedRadioButtonId());
                int conditionInt = rdgCondition.indexOfChild(radioButton);
                EditText edtDescription = (EditText) findViewById(R.id.edtDescription);
                //create new item from the input:
                Item item = new Item(0, 0, ItemType.fromInt(typeInt), ItemCondition.fromInt(conditionInt),
                        edtDescription.getText().toString() ,"check_test",new Date()); //TODO: put real info
                uploadTask.execute(item);
            }
        });

        bitmap = normalizeImage(path);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
        //get image orientation:


    }

    /**
     * Creates, scales and rotates an image to fit the ImageView
     *
     * @param path
     * @return
     */
    public Bitmap normalizeImage(String path){
        try{
            //get orientation
            ExifInterface exif = new ExifInterface(path);
            int orientation = Integer.parseInt(exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            //read raw image
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            //get size of screen:
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int dim = size.x/2;
            //resize image
            bitmap = Bitmap.createScaledBitmap(bitmap, dim, dim, true); //TODO: fix dimensions - rotate if needed
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void finishCallback(){
        finish();
    }
}
