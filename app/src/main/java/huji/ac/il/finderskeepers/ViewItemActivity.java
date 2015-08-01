package huji.ac.il.finderskeepers;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import  huji.ac.il.finderskeepers.data.*;


public class ViewItemActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_view_item);

        //setting the contents of the item
        Intent intent = getIntent();
        Item item = (Item) intent.getSerializableExtra("item");

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
}
