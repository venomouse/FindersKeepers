package huji.ac.il.finderskeepers;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;

import huji.ac.il.finderskeepers.data.Item;
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


    public static class AddItemTask extends AsyncTask<Item, Integer, String> {
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
            //showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(Item... items) {
            String serverImageID = ds.uploadImage(itemImage);
            items[0].setImageID(serverImageID);
            return  ds.addItem(items[0]);
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(String result) {
            bar.setVisibility(View.GONE);
            mActivity.finish();
        }
    }
}
