/**
 * 
 */
package it.rainbowbreeze.facedetector.ui;

import it.rainbowbreeze.facedetector.R;
import it.rainbowbreeze.facedetector.common.AppEnv;
import it.rainbowbreeze.facedetector.common.LogFacility;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * New face detection features
 *  http://developer.android.com/about/versions/android-4.0.html
 * 
 * 
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class MainMenuActivity extends Activity {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = MainMenuActivity.class.getSimpleName();

    private LogFacility mLogFacility;

    private ActivityHelper mActivityHelper;

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties


    // -------------------------------------------------- Events
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppEnv appEnv = AppEnv.i(getApplicationContext());
        mLogFacility = appEnv.getLogFacility();
        mLogFacility.logStartOfActivity(getClass(), savedInstanceState);
        mActivityHelper = appEnv.getActivityHelper();

        setContentView(R.layout.act_main_menu);
        
        assignMenuAction(R.id.actMainMenu_btnFacesFromImages, new View.OnClickListener() {
            public void onClick(View v) {
                mActivityHelper.openFacesFromImages(MainMenuActivity.this);
            }
        });
    }

    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods
    private void assignMenuAction(int buttonResourceId, View.OnClickListener listener) {
        View button = findViewById(buttonResourceId);
        if (null != button) {
            button.setOnClickListener(listener);
        } else {
            mLogFacility.e(LOG_HASH, "Button is unavailable: " + buttonResourceId);
        }
    }

    // ----------------------------------------- Private Classes
}
