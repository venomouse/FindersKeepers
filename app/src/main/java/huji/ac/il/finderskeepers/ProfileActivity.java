package huji.ac.il.finderskeepers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.User;
import huji.ac.il.finderskeepers.db.DataSource;
import huji.ac.il.finderskeepers.db.ItemDB;

public class ProfileActivity extends ActionBarActivity {
    private LatLng myLocation = null;
    private User user = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        myLocation = (LatLng) intent.getParcelableExtra("myLocation");
        final String userid = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("userid", null);

        Button btnSetAsHome = (Button) findViewById(R.id.btnSetAsHome);
        btnSetAsHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetAsHomeTask setAsHomeTask = new SetAsHomeTask(userid);
                setAsHomeTask.execute(myLocation);
            }
        });

        GetUserInfoTask getUserInfoTask = new GetUserInfoTask();
        getUserInfoTask.execute(userid);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    public class GetUserInfoTask extends AsyncTask<String, Integer, Integer> {
        List<Item> reportedItems = new ArrayList<>();
        List<Item> collectedItems = new ArrayList<>();
        List<Bitmap> reportedItemsImgs = new ArrayList<>();
        List<Bitmap> collectedItemsImgs = new ArrayList<>();

        String username;


        @Override
        protected void onPreExecute(){
            ProgressBar bar = (ProgressBar) ProfileActivity.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(String... userid) {
            DataSource ds = DataSource.getDataSource();
            User user  = ds.getUser(userid[0]);
            username = user.getUsername();
            List<String> reportedItemsIDs = user.getReportedItemsIDs();
            List<String> collectedItemsIDs = user.getCollectedItemsIDs();

            for (String itemid:reportedItemsIDs){
                Item currItem = ds.findItemByID(itemid);
                Bitmap currItemImg = ds.getImage(currItem.getImageID());
                reportedItems.add(currItem);
                reportedItemsImgs.add(currItemImg);
            }
            for (String itemid:collectedItemsIDs){
                Item currItem = ds.findItemByID(itemid);
                Bitmap currItemImg = ds.getImage(currItem.getImageID());
                collectedItems.add(currItem);
                collectedItemsImgs.add(currItemImg);
            }
            return 0;
        }

        protected void onPostExecute(Integer status) {
            ProgressBar bar = (ProgressBar) ProfileActivity.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);

            TextView lblUsername = (TextView) ProfileActivity.this.findViewById(R.id.lblUsername);
            lblUsername.setText(username);
            ListView lstReportedItems = (ListView) ProfileActivity.this.findViewById(R.id.listReported);
            ListView lstCollectedItems = (ListView) ProfileActivity.this.findViewById(R.id.listCollected);
            ListAdapter reportedItemsAdapter = new ItemListAdapter(ProfileActivity.this, R.layout.item_list_row, reportedItems,reportedItemsImgs);
            lstReportedItems.setAdapter(reportedItemsAdapter);

            ListAdapter collectedItemsAdapter = new ItemListAdapter(ProfileActivity.this, R.layout.item_list_row, collectedItems,collectedItemsImgs);
            lstCollectedItems.setAdapter(collectedItemsAdapter );

            LinearLayout layout = (LinearLayout) ProfileActivity.this.findViewById(R.id.layoutProfile);
            layout.setVisibility(View.VISIBLE);
        }

    }

    public class SetAsHomeTask extends AsyncTask<LatLng, Integer, Integer> {
        private String userid;

        public SetAsHomeTask(String userid){
            this.userid = userid;
        }

        @Override
        protected void onPreExecute(){
            ProgressBar bar = (ProgressBar) ProfileActivity.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(LatLng... location) {
            DataSource ds = DataSource.getDataSource();
            ds.setHomeLocation(userid, location[0]);

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("isHomeLocationSet", true)
                    .apply();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putFloat("homeLocationLat", (float) location[0].latitude)
                    .apply();


            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putFloat("homeLocationLng", (float) location[0].longitude)
                    .apply();
            return 0;
        }

        protected void onPostExecute(Integer status) {
            ProgressBar bar = (ProgressBar) ProfileActivity.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);
        }

    }
}
