package huji.ac.il.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.db.DataSource;

/**
 * A class for various async tasks used by the app
 *
 * Created by Paz on 8/10/2015.
 */
public class TaskManager {

    public static AddItemTask createAddItemTask(Activity mActivity, ProgressBar bar, File file){
        return new AddItemTask(mActivity,bar, file);
    }

    /**
     * This class adds an item (including an image) to the database
     */
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
            //TODO: Maria - why to we add this?
            intent.putExtra("item", item);
            intent.putExtra("message", "Item added successfully");
            mActivity.setResult(Activity.RESULT_OK,intent);
            mActivity.finish();
        }
    }

    /**
     * This task downloads the image to the database
     */
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

}
