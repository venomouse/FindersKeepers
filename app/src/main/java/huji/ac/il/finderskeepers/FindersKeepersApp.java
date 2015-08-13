package huji.ac.il.finderskeepers;

import android.app.Application;

import com.parse.Parse;


/**
 * Created by Paz on 8/8/2015.
 * This class is needed for initializing Parse only once in the application.
 */
public class FindersKeepersApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize global Parse settings
        Parse.initialize(this, "tlSBXlbDo127MbG97Ho7SmPT2lykEF5VQnhRV9np", "7CaFriBEOzaurDaJwtOtCn9vOxeB6iwsUsSUOE2F");
    }

}
