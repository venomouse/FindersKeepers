package huji.ac.il.finderskeepers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;
import huji.ac.il.finderskeepers.data.User;
import huji.ac.il.finderskeepers.db.DataSource;

public class AddItemActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        final String path = getIntent().getStringExtra("filepath");
        final Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(path);


        //get size of screen:
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int dim = size.x/2;


        Button btnAddItem = (Button) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskManager.AddItemTask uploadTask = TaskManager.createAddItemTask(getApplicationContext(), new File(path));
                RadioGroup rdgType = (RadioGroup) findViewById(R.id.rdgType);
                View radioButton = rdgType.findViewById(rdgType.getCheckedRadioButtonId());
                int typeInt = rdgType.indexOfChild(radioButton);
                RadioGroup rdgCondition = (RadioGroup) findViewById(R.id.rdgCondition);
                radioButton = rdgCondition.findViewById(rdgCondition.getCheckedRadioButtonId());
                int conditionInt = rdgCondition.indexOfChild(radioButton);
                Item item = new Item(0, 0, ItemType.fromInt(typeInt), ItemCondition.fromInt(conditionInt),"check_test",new Date()); //TODO: put real info
                uploadTask.execute(item);
            }
        });

        //get image orientation:
        try{
            ExifInterface exif = new ExifInterface(path);
            exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, dim, dim, true); //TODO: fix dimensions - rotate if needed
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(resized);
        }
        catch (Exception e){
            Log.d("image orintation", e.getMessage());
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
}
