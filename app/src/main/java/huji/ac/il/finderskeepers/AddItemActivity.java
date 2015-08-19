package huji.ac.il.finderskeepers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ExifInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class AddItemActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        String path = getIntent().getStringExtra("filepath");
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(path);

        //get size of screen:
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int dim = size.x/2;

        //get image orientation:
        try{
            ExifInterface exif = new ExifInterface(path);
            exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, dim, dim, true); //TODO: fix dimensions
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
