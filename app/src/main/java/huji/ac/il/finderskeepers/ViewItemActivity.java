package huji.ac.il.finderskeepers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.db.DataSource;

/**
 * This activity is used to display the details of the item
 * It is called by a click on a marker on a map.
 */
public class ViewItemActivity extends CompletableActivity {

    private LatLng  myLocation = null;
    private Item item = null;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_item);

        bar = (ProgressBar) this.findViewById(R.id.progressBar);

        //setting the contents of the item
        Intent intent = getIntent();
        item = intent.getParcelableExtra("item");
        myLocation = intent.getParcelableExtra("myLocation");
        fillItemProperties();

        //setting up the buttons
        Button btnViewItemTakeMeThere = (Button) findViewById(R.id.viewItemTakeMeThereBtn);
        btnViewItemTakeMeThere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakeMeThereClick();
            }
        });

        Button btnViewItemBack = (Button) findViewById(R.id.viewItemBackBtn);
        btnViewItemBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });

        TaskManager.GetImageTask getImageTask = new TaskManager.GetImageTask(this,bar);
        getImageTask.execute(item.getImageID());
    }

    /**
     * Fills the properties of the displayed items into the fields
     */
    private void fillItemProperties() {

        DataSource ds = DataSource.getDataSource();
        TextView reporter = (TextView) findViewById(R.id.viewItem_reporter);
        reporter.setText(ds.getReporter(item).getUsername());

        TextView reportTime = (TextView) findViewById(R.id.viewItem_reportTime);
        reportTime.setMaxWidth(reportTime.getWidth());
        reportTime.setText(DateUtils.getRelativeDateTimeString(getBaseContext(),
                item.getCreationDate().getTime(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.WEEK_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE));

        ImageView typeIcon = (ImageView) findViewById(R.id.typeIcon);
        typeIcon.setImageResource(item.getType().iconID);

        RatingBar conditionBar = (RatingBar) findViewById(R.id.viewItem_conditionRatingBar);
        conditionBar.setRating((float) item.getCondition().value);

        TextView subtypeText = (TextView) findViewById(R.id.subtypeText);
        subtypeText.setText(item.getDescription());

        if (item.getDescription().isEmpty()) {
            subtypeText.setText("(no description)");
            subtypeText.setTextColor(Color.LTGRAY);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_item, menu);
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

    private void onTakeMeThereClick(){
        Intent takeMeThereIntent = new Intent(this,TakeMeThereActivity.class);
        takeMeThereIntent.putExtra("item",item);
        takeMeThereIntent.putExtra("from", myLocation);
        takeMeThereIntent.putExtra("to", new LatLng(item.getLatitude(), item.getLongitude()));
        startActivity(takeMeThereIntent);
    }

    private void onBackClick () {
        this.finish();
    }

    public void complete(Object object){
        Bitmap bitmap = (Bitmap) object;
        ImageView imageView = (ImageView) findViewById(R.id.itemImage);
        imageView.setImageBitmap(bitmap);
        LinearLayout layout = (LinearLayout) findViewById(R.id.activityViewItem);
        layout.setVisibility(View.VISIBLE);
    }
}
