package huji.ac.il.finderskeepers;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.Date;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;
import huji.ac.il.finderskeepers.db.DataSource;

/**
 * This activity is used to add an item with existing picture to the database
 */
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

                RatingBar conditionRatingBar = (RatingBar) findViewById(R.id.addItemConditionRatingBar);
                int conditionInt = (int) conditionRatingBar.getRating();

                EditText edtDescription = (EditText) findViewById(R.id.edtDescription);

                LatLng myLocation = getIntent().getParcelableExtra("myLocation");

                String userid = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("userid", null);

                Item item = new Item(true,myLocation.latitude, myLocation.longitude, ItemType.fromInt(typeInt), ItemCondition.fromInt(conditionInt),
                        edtDescription.getText().toString() ,userid ,new Date());
                uploadTask.execute(item);
            }
        });

        bitmap = DataSource.normalizeImage(path,300,300,true);
        ImageView imageView = (ImageView) findViewById(R.id.itemImage);
        imageView.setImageBitmap(bitmap);


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
