package huji.ac.il.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;

import  huji.ac.il.finderskeepers.data.*;


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
        item = (Item) intent.getSerializableExtra("item");
        myLocation = (LatLng) intent.getParcelableExtra("myLocation");



        Button btnViewItemTakeMeThere = (Button) findViewById(R.id.viewItemTakeMeThereBtn);
        btnViewItemTakeMeThere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTakeMeThereClick(v);
            }
        });

        Button btnViewItemBack = (Button) findViewById(R.id.viewItemBackBtn);
        btnViewItemBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick(v);
            }
        });

        TaskManager.GetImageTask getImageTask = new TaskManager.GetImageTask(this,bar);
        getImageTask.execute(item.getImageID());
    }

    private void fillItemProperties() {

        ImageView typeIcon = (ImageView) findViewById(R.id.typeIcon);
        typeIcon.setImageResource(item.getType().iconID);

        ImageView conditionIcon = (ImageView) findViewById(R.id.conditionIcon);
        conditionIcon.setImageResource(item.getCondition().iconID);


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

    private void onTakeMeThereClick(View v){
        Intent takeMeThereIntent = new Intent(this,TakeMeThereActivity.class);

        takeMeThereIntent.putExtra("from", myLocation);
        takeMeThereIntent.putExtra("to", new LatLng(item.getLatitude(), item.getLongitude()));

        startActivity(takeMeThereIntent);
    }

    private void onBackClick (View v) {
        this.finish();
    }

    public void complete(Object object){
        fillItemProperties();
        Bitmap bitmap = (Bitmap) object;
        ImageView imageView = (ImageView) findViewById(R.id.itemImage);
        imageView.setImageBitmap(bitmap);
        LinearLayout layout = (LinearLayout) findViewById(R.id.activityViewItem);
        layout.setVisibility(View.VISIBLE);
    }
}
