/**
 * 
 */
package it.rainbowbreeze.facedetector.ui;

import it.rainbowbreeze.facedetector.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author Alfredo "Rainbowbreeze" Morresi
 *
 */
public class FaceImageView extends ImageView {
    // ------------------------------------------ Private Fields
    private static final String LOG_HASH = FaceImageView.class.getSimpleName();
    
    private FaceDetector.Face[] mDetectedFaces;
    private Bitmap mGlassesOriginal;
    private Paint mRedOverlayPaint;
    private Paint mBitmapPaint;


    // -------------------------------------------- Constructors
    public FaceImageView(Context context) {
        super(context);
        initVars(context);
    }

    public FaceImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVars(context);
    }

    public FaceImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initVars(context);
    }
    
    // --------------------------------------- Public Properties

    // -------------------------------------------------- Events
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (null == mDetectedFaces) return;
        for (FaceDetector.Face face : mDetectedFaces) {
//            addFaceOverlay(face, canvas, mRedOverlayPaint);
//            addEyesOverlay(face, canvas, mRedOverlayPaint);
            addGlassesOverlay(face, canvas, mBitmapPaint);
        }
    }

    // ------------------------------------------ Public Methods
    public void resetFaces() {
        mDetectedFaces = null;
    }
    
    public void setFaces(FaceDetector.Face[] newFaces) {
        mDetectedFaces = newFaces;
    }


    // ----------------------------------------- Private Methods
    private void initVars(Context context) {
        mRedOverlayPaint = new Paint();
        mRedOverlayPaint.setColor(Color.rgb(255, 0, 0));
        mRedOverlayPaint.setAlpha(150);
        mRedOverlayPaint.setStrokeWidth(5);
        
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);
        mBitmapPaint.setDither(true);
        
        mGlassesOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.glasses);
    }
    
    private PointF migratePoint(Canvas canvas, PointF point) {
//        int canvasWidth = canvas.getWidth();
//        int canvasHeight = canvas.getHeight();
//        PointF scaledPoint = new PointF(point.x * canvasWidth / mImageWidth, point.y * canvasHeight / mImageHeight);
        return point;
    }

    private void addFaceOverlay(FaceDetector.Face face, Canvas canvas, Paint paint) {
        PointF point = new PointF();
        face.getMidPoint(point);
        point = migratePoint(canvas, point);
        RectF rect = new RectF(point.x - 50, point.y - 50, point.x + 50, point.y + 50);
        canvas.drawRect(rect, paint);
    }

    private void addEyesOverlay(FaceDetector.Face face, Canvas canvas, Paint paint) {
        PointF point = new PointF();
        face.getMidPoint(point);
        float eyeDistance = face.eyesDistance();
        //find glasses width
        float eyeWidth = eyeDistance / 2;
        float eyeHeight = eyeDistance / 4;
        RectF rect = new RectF(
                point.x - eyeDistance / 2 - eyeWidth / 2,
                point.y - eyeHeight,
                point.x - eyeDistance / 2 + eyeWidth / 2,
                point.y + eyeHeight);
        canvas.drawOval(rect, paint);
        rect = new RectF(
                point.x + eyeDistance / 2 - eyeWidth / 2,
                point.y - eyeHeight,
                point.x + eyeDistance / 2 + eyeWidth / 2,
                point.y + eyeHeight);
        canvas.drawOval(rect, paint);
    }

    private void addGlassesOverlay(FaceDetector.Face face, Canvas canvas, Paint paint) {
        PointF point = new PointF();
        face.getMidPoint(point);
        float eyeDistance = face.eyesDistance();
        
        Bitmap scaledGlasses = Bitmap.createScaledBitmap(mGlassesOriginal, (int)(eyeDistance * 2), (int)(eyeDistance / 2), true);
        canvas.drawBitmap(scaledGlasses, point.x - (eyeDistance),  point.y - (eyeDistance / 4), paint);
    }

    // ----------------------------------------- Private Classes
}
