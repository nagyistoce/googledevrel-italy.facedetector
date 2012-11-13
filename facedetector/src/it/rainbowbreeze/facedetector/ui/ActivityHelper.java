/**
 * 
 */
package it.rainbowbreeze.facedetector.ui;

import android.app.Activity;
import android.content.Context;
import it.rainbowbreeze.libs.common.IRainbowLogFacility;
import it.rainbowbreeze.libs.ui.RainbowActivityHelper;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class ActivityHelper extends RainbowActivityHelper {

    // ------------------------------------------ Private Fields

    // -------------------------------------------- Constructors
    public ActivityHelper(IRainbowLogFacility logFacility, Context context) {
        super(logFacility, context);
    }

    // --------------------------------------- Public Properties

    // ------------------------------------------ Public Methods
    public void openFacesFromImages(Activity callerActivity) {
        openActivity(callerActivity, FacesFromImagesActivity.class, null, false, REQUESTCODE_NONE);
    }

    // ----------------------------------------- Private Methods

    // ----------------------------------------- Private Classes
}
