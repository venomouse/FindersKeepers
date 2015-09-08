package huji.ac.il.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;
import huji.ac.il.finderskeepers.db.DataSource;

/**
 * A class for various async tasks
 *
 * Created by Paz on 8/10/2015.
 */
public class TaskManager {

    public static AddItemTask createAddItemTask(Activity mActivity, ProgressBar bar, File file){
        return new AddItemTask(mActivity,bar, file);
    }


    public static class AddItemTask extends AsyncTask<Item, Integer, Item> {
        private File itemImage;
        private Activity mActivity;

        private ProgressBar bar;

        public AddItemTask(Activity mActivity,ProgressBar bar, File itemImage){
            this.itemImage = itemImage;
            this.bar = bar;
            this.mActivity = mActivity;
        }
        private DataSource ds = DataSource.getDataSource();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = (ProgressBar) mActivity.findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Item doInBackground(Item... items) {
            String serverImageID = ds.uploadImage(itemImage);
            items[0].setImageID(serverImageID);
            String itemId = ds.addItem(items[0]);
            items[0].setId(itemId);
            ds.addToReportedItems(items[0].getReporterID(),itemId);
            return  items[0];
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(Item item) {
            bar.setVisibility(View.GONE);
            Intent intent = new Intent();
            intent.putExtra("item", item);
            mActivity.setResult(Activity.RESULT_OK,intent);
            mActivity.finish();
        }
    }

    public static class GetImageTask extends AsyncTask<String, Integer, String> {
        private CompletableActivity activity;
        private Bitmap bitmap;
        private ProgressBar bar;
        private DataSource ds = DataSource.getDataSource();

        public GetImageTask(CompletableActivity activity,ProgressBar bar){
            this.bar = bar;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar = (ProgressBar) activity.findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... imageID) {
            bitmap = ds.getImage(imageID[0]);
            return "";
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String result) {
            bar.setVisibility(View.GONE);
            activity.complete(bitmap);
        }
    }

    public static class FindItemsTask extends AsyncTask<Void, Integer, ArrayList<Item>> {
        FindItemActivity activity;
        ItemCondition minimalCondition;
        ItemType type;
        LatLng fromPoint;
        double distance;
        String description;

        public FindItemsTask(FindItemActivity activity, ItemType type, ItemCondition minimalCondition,
               LatLng fromPoint, double distance, String description) {
            this.activity = activity;
            this.minimalCondition = minimalCondition;
            this.type = type;
            this.fromPoint = fromPoint;
            this.distance = distance;
            this.description = description;
        }

        @Override
        protected ArrayList<Item> doInBackground(Void... params) {
            DataSource ds = DataSource.getDataSource();
            return ds.findItems(type, minimalCondition, fromPoint, distance,description);
        }


        protected void onPostExecute(ArrayList<Item> result) {
            activity.complete(result);
        }

    }

}
