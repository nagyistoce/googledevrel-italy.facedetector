package it.rainbowbreeze.facedetector.ui;

import it.rainbowbreeze.facedetector.R;
import it.rainbowbreeze.facedetector.common.AppEnv;
import it.rainbowbreeze.facedetector.common.LogFacility;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Some tutorials:
 *  http://www.developer.com/ws/android/programming/face-detection-with-android-apis.html
 * 
 * Reference
 *  http://developer.android.com/reference/android/media/FaceDetector.html
 * 
 * @author Alfredo "Rainbowbreeze" Morresi
 */
public class FacesFromImagesActivity extends Activity {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = FacesFromImagesActivity.class.getSimpleName();

    private LogFacility mLogFacility;
    private FaceImageView mImgFaces;
    private Button mBtnPrev;
    private Button mBtnNext;
    private Button mBtnAnalyze;
    
    private int mCurrentImageIndex = -1;
    private int[] mImageIds = {
            R.drawable.face1,
            R.drawable.face2,
            R.drawable.face3,
            R.drawable.face4,
            R.drawable.face5,             
            R.drawable.face6, 
            R.drawable.dogface1};

    private TextView mLblLog;

    // -------------------------------------------- Constructors

    // --------------------------------------- Public Properties
    
    // -------------------------------------------------- Events

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AppEnv appEnv = AppEnv.i(getApplicationContext());
        mLogFacility = appEnv.getLogFacility();
        mLogFacility.logStartOfActivity(getClass(), savedInstanceState);
        
        setContentView(R.layout.act_faces_from_images);
        
        mImgFaces = (FaceImageView) findViewById(R.id.actFacesFromImages_imgFaces);
        mLblLog = (TextView) findViewById(R.id.actFacesFromImages_lblLog);
        mBtnAnalyze = (Button) findViewById(R.id.actFacesFromImages_btnAnalyze);
        mBtnAnalyze.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                analyzeCurrentImage();
            }
        });
        mBtnPrev = (Button) findViewById(R.id.actFacesFromImages_btnPrevImage);
        mBtnPrev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveCurrentImage(-1);
            }
        });
        mBtnNext = (Button) findViewById(R.id.actFacesFromImages_btnNextImage);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                moveCurrentImage(+1);
            }
        });
        
        setCurrentImage(0);
        setNavigationStatus();
    }


    // ------------------------------------------ Public Methods

    // ----------------------------------------- Private Methods
    
    protected void analyzeCurrentImage() {
        FaceAnalyzerTask faceAnalyzerTask = new FaceAnalyzerTask(mLblLog, mBtnAnalyze, mImgFaces);
        faceAnalyzerTask.execute(mImageIds[mCurrentImageIndex]);
    }


    private void setCurrentImage(int newImageIndex) {
        mLogFacility.v(LOG_HASH, "Setting new image to index " + newImageIndex);
        if (newImageIndex < 0 || newImageIndex > mImageIds.length - 1) {
            mLogFacility.i(LOG_HASH, "Cannot set the requested index");
            return;
        }
        mImgFaces.resetFaces();
        mCurrentImageIndex = newImageIndex;
        //clean the log
        mLblLog.setText(null);
        mImgFaces.setImageResource(mImageIds[mCurrentImageIndex]);
    }

    private void moveCurrentImage(int offset) {
        setCurrentImage(mCurrentImageIndex + offset);
        setNavigationStatus();
    }

    private void setNavigationStatus() {
        if (mCurrentImageIndex <= 0) {
            mBtnPrev.setEnabled(false);
            mBtnNext.setEnabled(true);
        } else if (mCurrentImageIndex >= (mImageIds.length -1)) {
            mBtnPrev.setEnabled(true);
            mBtnNext.setEnabled(false);
        } else {
            mBtnPrev.setEnabled(true);
            mBtnNext.setEnabled(true);
        }
    }
    
    // ----------------------------------------- Private Classes

    private class FaceAnalyzerTask extends AsyncTask<Integer, String, FaceDetector.Face[]> {
        final private TextView mLblLogConsole;
        final private Button mBtnAnalyze;
        final private FaceImageView mImgFaces;
        
        public FaceAnalyzerTask(TextView lblLogConsole, Button btnAnalyze, FaceImageView imgFaces) {
            mLblLogConsole = lblLogConsole;
            mBtnAnalyze = btnAnalyze;
            mImgFaces = imgFaces;
        }
        
        @Override
        protected void onPreExecute() {
            mBtnAnalyze.setEnabled(false);
            publishProgress("Loading image");
        }
        
        @Override
        protected Face[] doInBackground(Integer... params) {
            if (null == params || params.length <= 0) return null;
            
            int imageResourceId = params[0];

            //get the current image
            Bitmap fullColorBmp = BitmapFactory.decodeResource(getResources(), imageResourceId);
            //dirty way, improve it!
            Bitmap bmp = fullColorBmp.copy(Bitmap.Config.RGB_565, true);
            fullColorBmp.recycle();

            //analyzes it
            publishProgress("Scanning for faces");
            int maxFaces = 10;
            mLogFacility.v(LOG_HASH, "Bitmap to analize width, height:" + bmp.getWidth() + ", " + bmp.getHeight());
            FaceDetector faceDetector = new FaceDetector(bmp.getWidth(), bmp.getHeight(), maxFaces);
            FaceDetector.Face[] faces = new FaceDetector.Face[maxFaces];
            int count = faceDetector.findFaces(bmp, faces);
            
            if (count == 0) return null;
            
            FaceDetector.Face[] recognizedFaces = new FaceDetector.Face[count];
            for (int i=0; i<count; i++) {
                recognizedFaces[i] = faces[i];
            }
            return recognizedFaces;
        }
        
        @Override
        protected void onPostExecute(Face[] faces) {
            mBtnAnalyze.setEnabled(true);
            if (null == faces) {
                publishProgress("No faces found");
                return;
            }
            publishProgress("Found " + faces.length + " faces");
            
            for (FaceDetector.Face face : faces) {
                PointF point = new PointF();
                face.getMidPoint(point);
                mLogFacility.v(LOG_HASH, "Face data point: " + point.x + "," + point.y);
            }
            
            //draws new faces
            mImgFaces.setFaces(faces);
            mImgFaces.invalidate();
        }
        
        @Override
        protected void onProgressUpdate(String... values) {
            if (null == values || values.length <= 0) return;
            String message = values[0];
            mLogFacility.v(LOG_HASH, message);
            mLblLogConsole.setText(message);
        }

    }
}
