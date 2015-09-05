package huji.ac.il.finderskeepers;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import huji.ac.il.finderskeepers.data.Item;
import huji.ac.il.finderskeepers.data.ItemCondition;
import huji.ac.il.finderskeepers.data.ItemType;
import huji.ac.il.finderskeepers.data.User;
import huji.ac.il.finderskeepers.db.DataSource;


public class SplashScreen extends ActionBarActivity {
    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        checkLoggedIn();

        Button btnSignIn = (Button) findViewById(R.id.btnSignUp);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.edtUsername);
                SignUpTask signUpTask = new SignUpTask();
                signUpTask.execute(tv.getText().toString());
            }
        });
    }

    public void checkLoggedIn() {
        LinearLayout linearLayout = (LinearLayout) this.findViewById(R.id.singinForm);

        boolean isLoggedIn = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("isLoggedIn", false);
        if (!isLoggedIn){
            // show sign up section:
            linearLayout.setVisibility(View.VISIBLE);
        }
        else{
            //hide sign up section
            linearLayout.setVisibility(View.INVISIBLE);

            //get user's home location
            String userid = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("userid", null);
            GetUserTask getUserTask = new GetUserTask();
            getUserTask.execute(userid);

//            //wait some time (for branding purposes) and then continue to main screen
//            ProgressBar bar = (ProgressBar) this.findViewById(R.id.progressBar);
//            bar.setVisibility(View.VISIBLE);
//            /* New Handler to start the MainScreenActivity
//            * and close this SplashScreen after some seconds.*/
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                /* Create an Intent that will start the Menu-Activity. */
//                    Intent mainIntent = new Intent(SplashScreen.this, MainScreenActivity.class);
//                    startActivity(mainIntent);
//                    SplashScreen.this.finish();
//                }
//            }, SPLASH_DISPLAY_LENGTH);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
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


    public class SignUpTask extends AsyncTask<String, Integer, User> {

        @Override
        protected void onPreExecute(){
            ProgressBar bar = (ProgressBar) SplashScreen.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected User doInBackground(String... params) {
            User user = DataSource.getDataSource().addUser(params[0], User.DEFAULT_LOCATION);
            return user;
        }

        protected void onPostExecute(User user) {
            ProgressBar bar = (ProgressBar) SplashScreen.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);

            if (user == null){
                TextView lblSingUpStatus = (TextView ) SplashScreen.this.findViewById(R.id.lblSignUpStatus);
                lblSingUpStatus.setVisibility(View.VISIBLE);
            }
            else{
                //update that we're logged in
                getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .edit()
                        .putBoolean("isLoggedIn", true)
                        .apply();
                //update the logged in user
                getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .edit()
                        .putString("userid", user.getId())
                        .apply();
                //jump to main screen
                Intent mainIntent = new Intent(SplashScreen.this, MainScreenActivity.class);
                mainIntent.putExtra("homeLocation",user.getHomeLocation());
                startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }

    }

    public class GetUserTask extends AsyncTask<String, Integer, User> {

        @Override
        protected void onPreExecute(){
            ProgressBar bar = (ProgressBar) SplashScreen.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected User doInBackground(String... userid) {
            return DataSource.getDataSource().getUser(userid[0]);
        }

        protected void onPostExecute(User user) {
            ProgressBar bar = (ProgressBar) SplashScreen.this.findViewById(R.id.progressBar);
            bar.setVisibility(View.INVISIBLE);
            Intent mainIntent = new Intent(SplashScreen.this, MainScreenActivity.class);
            mainIntent.putExtra("homeLocation",user.getHomeLocation());
            startActivity(mainIntent);
            SplashScreen.this.finish();

        }

    }
}
