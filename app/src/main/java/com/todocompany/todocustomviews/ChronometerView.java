package com.todocompany.todocustomviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Andriy on 02.07.2015.
 */
public class ChronometerView extends View {

    private final float DEFAULT_WIDTH = 150, DEFAULT_HEIGHT = 150;
    private final int DEFAULT_TIME = 15000;
    private final int DEFAULT_FADING_SPEED = 60;
    private final int DEFAULT_CIRCLE_COLOR = Color.CYAN;
    private final int DEFAULT_FILL_COLOR = Color.RED;

    private float xCenter, yCenter;
    private int resultWidth;
    private int resultHeight;

    private float toAlphaValue = 0;
    private float progressInPercents = 0;
    private float progressInDegrees = 0;
    private int timeToProcess = DEFAULT_TIME;
    private int fadingSpeed = DEFAULT_FADING_SPEED;

    private Paint circlePaint;
    private Paint arrowPaint;
    private RectF chronometerBoundsRect;
    private int circleColor = DEFAULT_CIRCLE_COLOR;
    private int fillColor = DEFAULT_FILL_COLOR;

    private DrawingTask drawingTask;
    private ChronometerCallback chronometerCallback;


    private Context context;
    // Delay between callbacks
    int callbacksDelay;
    int currentDelay;
    long startTime;


    public ChronometerView(Context context) {
        super(context);
        initPaints();

    }

    public ChronometerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getInfoFromAttributes(attrs);
        initPaints();

    }

    public ChronometerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getInfoFromAttributes(attrs);
        initPaints();



    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        canvas.drawCircle(xCenter, yCenter, resultWidth / 2, circlePaint);

        canvas.drawArc(chronometerBoundsRect, 270, progressInDegrees, true, arrowPaint);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        resultWidth = MeasureUtils.getMeasurement(widthMeasureSpec, 150);
        resultHeight = MeasureUtils.getMeasurement(heightMeasureSpec, 150);
        xCenter = resultWidth / 2;
        yCenter = resultHeight / 2;

        setMeasuredDimension(resultWidth, resultHeight);
    }



    public void start() {
        drawingTask = new DrawingTask();
            progressInDegrees = 0;
            toAlphaValue = 0;
            drawingTask.execute();

    }


    private void getInfoFromAttributes(AttributeSet attrs) {


        TypedArray ta = getContext().obtainStyledAttributes( attrs, R.styleable.cv, 0, 0 );

        try {
            timeToProcess = ta.getInt(0, DEFAULT_TIME);
            fadingSpeed = ta.getInt(1, DEFAULT_FADING_SPEED);
            circleColor = ta.getColor(2, DEFAULT_CIRCLE_COLOR);
            fillColor = ta.getInt(3, DEFAULT_FILL_COLOR);

        } finally {
            ta.recycle();
        }





//        TypedArray a = context.obtainStyledAttributes(attrs,
//                R.styleable.cv);
//
//        final int N = a.getIndexCount();
//        for (int i = 0; i < N; ++i) {
//            int attr = a.getIndex(i);
//            switch (attr) {
//                case R.styleable.cv_timeRange:
//                    timeToProcess = a.getInt(attr, DEFAULT_TIME);
//                    break;
//                case R.styleable.cv_fadingSpeed:
//                    fadingSpeed = a.getInt(attr, DEFAULT_FADING_SPEED);
//                    break;
//                case R.styleable.cv_circleColor:
//                    circleColor = a.getColor(attr, DEFAULT_CIRCLE_COLOR);
//                    break;
//                case R.styleable.cv_fillingColor:
//                    fadingSpeed = a.getInt(attr, DEFAULT_FILL_COLOR);
//                    break;
//
//
//            }
//        }
//        a.recycle();

    }

    /** Initialization for paints*/
    private void initPaints() {
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);
        circlePaint.setStrokeWidth(4);
        circlePaint.setAntiAlias(true);

        arrowPaint = new Paint();
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setColor(fillColor);
        arrowPaint.setStrokeWidth(5);
        arrowPaint.setAntiAlias(true);

        chronometerBoundsRect = new RectF(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);


    }


    public long getTimeRange() {
        return timeToProcess;
    }

    /** @param timeRangeInSeconds How much time must take filling of chronometer */
    public void setTimeRange(int timeRangeInSeconds) {
        if (timeRangeInSeconds != 0) {
            timeToProcess = timeRangeInSeconds * 1000;
        }
    }

    /** @param delay Callback will call onDelayTick method every delay seconds */
    public void setCallbacksDelay(int delay) {
        callbacksDelay = delay * 1000;
    }

    public void setChronometerCallback(ChronometerCallback chronometerCallback) {
        this.chronometerCallback = chronometerCallback;
    }



    private class DrawingTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            startTime = System.currentTimeMillis();
            currentDelay = callbacksDelay;

            // paint set to default, without alpha
            arrowPaint.setColor(fillColor);
            // Filling circle animation
            while (progressInDegrees <= 360) {

                if (chronometerCallback != null | callbacksDelay == 0) {
                    if (System.currentTimeMillis() - startTime >= currentDelay) {

                        publishProgress(currentDelay);

                        currentDelay += callbacksDelay;


                    }
                }

                try {
                    Thread.sleep(timeToProcess / 360);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                progressInDegrees++;
                postInvalidate();

            }



            // Fading animation
            while (toAlphaValue < fadingSpeed) {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                float alphaProgress = toAlphaValue / fadingSpeed * 100;
                int alpha = (int) ((100 - alphaProgress) * 255 / 100);
                arrowPaint.setAlpha(alpha);
                toAlphaValue++;
                postInvalidate();
            }

            return null;
        }




        @Override
        protected void onProgressUpdate (Integer...values){
            super.onProgressUpdate(values);
            if (chronometerCallback != null) {
                chronometerCallback.onDelayTick(values[0] / 1000);
            }

        }

        @Override
        protected void onPostExecute (Void result){
            super.onPostExecute(result);
            if (chronometerCallback != null){
                chronometerCallback.onDone();

        }
    }
}

    public interface ChronometerCallback {

        void onDone();

        void onDelayTick(int onDelay);

    }

}
