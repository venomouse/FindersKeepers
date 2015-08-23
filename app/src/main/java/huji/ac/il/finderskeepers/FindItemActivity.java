package huji.ac.il.finderskeepers;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.parse.ParseQuery;


public class FindItemActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item);

        Button btnAddItem = (Button) findViewById(R.id.btnAddItem);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFindClick(v);
            }
        });
    }

    private void onFindClick(View v) {
        RadioGroup findItemTypeRdg = (RadioGroup) findViewById(R.id.findItemTypeRdg);
        View typeRadioButton = findItemTypeRdg.findViewById(findItemTypeRdg.getCheckedRadioButtonId());
        int typeInt = findItemTypeRdg.indexOfChild(typeRadioButton);

        RadioGroup findItemConditionRdg = (RadioGroup) findViewById(R.id.findItemConditionRdg);
        View conditionRadioButton = findItemConditionRdg.findViewById(findItemConditionRdg.getCheckedRadioButtonId());
        int conditionInt = findItemConditionRdg.indexOfChild(conditionRadioButton);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find_item, menu);
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
