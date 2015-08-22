package huji.ac.il.finderskeepers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

import huji.ac.il.finderskeepers.MainScreenActivity;
import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.db.DataSource;

/**
 * A class for various async tasks
 *
 * Created by Paz on 8/10/2015.
 */
public class TaskManager {

    public static AddItemTask createAddItemTask(Activity activity, File file){
        return new AddItemTask(activity, file);
    }


    public static class AddItemTask extends AsyncTask<Item, Integer, String> {
        private File itemImage;
        private Activity activity;
        public AddItemTask(Activity activity, File itemImage){
            this.itemImage = itemImage;
            this.activity = activity;
        }
        private DataSource ds = new DataSource();
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(Item... items) {
            String serverImageID = ds.uploadImageToParse(itemImage);
            items[0].setImageID(serverImageID);
            return  ds.addItem(items[0]);
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(activity, "Item added!",Toast.LENGTH_LONG);
        }
    }

   /* private static class UploadImageTask extends AsyncTask<File, Integer, String> {
        Context myContext;
        private DataSource ds = new DataSource();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(File... files) {
            return  ds.uploadImageToParse(files[0]);
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String result) {

        }
    }*/

}
