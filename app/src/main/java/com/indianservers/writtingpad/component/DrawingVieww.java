package com.indianservers.writtingpad.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.indianservers.writtingpad.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class DrawingVieww extends View implements View.OnTouchListener
{
	public static final int SELECT = 0;
	public static final int LINE = 1;
	public static final int SMOOTHLINE = 2;
	public static final int RECTANGLE = 3;
	public static final int SQUARE = 4;
	public static final int CIRCLE = 5;
	public static final int TRIANGLE = 6;
	public int mCurrentShape;
	protected float mStartX,mStartY;
	protected float mx, my;
	protected boolean isDrawing = false;
	private Path mDrawPath;
	private Paint mBackgroundPaint;
	private Paint mDrawPaint;
	public  Canvas mDrawCanvas;
	private Bitmap mCanvasBitmap;
	private int width, height;
	int mWidth, mHeight;
	private ArrayList<Path> mPaths = new ArrayList<>();
	private ArrayList<Paint> mPaints = new ArrayList<>();
	public ArrayList<Path> mUndonePaths = new ArrayList<>();
	public ArrayList<Paint> mUndonePaints = new ArrayList<>();
	private ArrayList<Pair<Path, Paint>> paths = new ArrayList<Pair<Path, Paint>>();
	// Set default values
	private int mBackgroundColor = ContextCompat.getColor(getContext(), android.R.color.black);
	private int mPaintColor = ContextCompat.getColor(getContext(), android.R.color.white);
	private int mStrokeWidth = 5;
	private boolean isEraserActive = false;
	private static final float TOUCH_TOLERANCE = 4;
	private Rect mMeasuredRect;

	/** All available circles */
	private ArrayList<CircleArea> mCircles = new ArrayList<DrawingVieww.CircleArea>();
	ArrayList<DrawingVieww.CircleArea> circle = new ArrayList<>();
	private SparseArray<DrawingVieww.CircleArea> mCirclePointer = new SparseArray<DrawingVieww.CircleArea>();
	public DrawingVieww(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	public DrawingVieww(Context context){
		super(context);
	}
	public DrawingVieww(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	private void init()
	{
		mDrawPath = new Path();
		mBackgroundPaint = new Paint();
		initPaint();
	}
	private void initPaint()
	{
		mDrawPaint = new Paint();
		mDrawPaint.setColor(mPaintColor);
		mDrawPaint.setAntiAlias(true);
		mDrawPaint.setStrokeWidth(mStrokeWidth);
		mDrawPaint.setStyle(Paint.Style.STROKE);
		mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
		mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mx = event.getX();
		my = event.getY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				initPaint();
				break;
		}
		switch (mCurrentShape) {
			case SELECT:
				onTouchEventCirclee(event);
				break;
			case LINE:
				onTouchEventLine(event);
				break;
			case SMOOTHLINE:
				onTouchEventSmoothLine(event);
				break;
			case RECTANGLE:
				onTouchEventRectangle(event);
				break;
			case SQUARE:
				onTouchEventSquare(event);
				break;
			case CIRCLE:
				onTouchEventCircle(event);
				break;
			case TRIANGLE:
				onTouchEventTriangle(event);
				break;
		}
		return true;
	}
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mDrawCanvas = new Canvas(mCanvasBitmap);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mCanvasBitmap, 0, 0, mDrawPaint);
		drawBackground(canvas);
		drawPaths(canvas);
		for (Pair<Path, Paint> p : paths) {
			canvas.drawPath(p.first, p.second);
		}
		canvas.drawBitmap(mCanvasBitmap, null, mMeasuredRect, null);

		for (DrawingVieww.CircleArea circle : mCircles) {
			canvas.drawCircle(circle.centerX, circle.centerY, circle.radius, mDrawPaint);
		}
		canvas.drawPath(mDrawPath,mDrawPaint);

		if (isDrawing){
			switch (mCurrentShape) {
				case SELECT:
					onDrawCircle(canvas);
					break;
				case LINE:
					onDrawLine(canvas);
					break;
				case RECTANGLE:
					onDrawRectangle(canvas);
					break;
				case SQUARE:
					onDrawRectangle(canvas);
					break;
				case CIRCLE:
					onDrawCircle(canvas);
					break;
				case TRIANGLE:
					onDrawTriangle(canvas);
					break;
			}
		}
	}
	public void drawBackground(Canvas canvas) {
		mBackgroundPaint.setColor(mBackgroundColor);
		mBackgroundPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), mBackgroundPaint);
	}
	private void drawPaths(Canvas canvas) {
		int i = 0;
		for (Path p : mPaths)
		{
			canvas.drawPath(p, mPaints.get(i));
			i++;
		}
	}

	public boolean onTouchEventCirclee(final MotionEvent event) {
		boolean handled = false;

		CircleArea touchedCircle;
		int xTouch;
		int yTouch;
		int pointerId;
		int actionIndex = event.getActionIndex();

		// get touch event coordinates and make transparent circle from it
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:

					xTouch = (int) event.getX(0);
					yTouch = (int) event.getY(0);
					// check if we've touched inside some circle
					touchedCircle = obtainTouchedCircle(xTouch, yTouch);
					mCirclePointer.put(event.getPointerId(0), touchedCircle);
					invalidate();
					handled = true;
				break;

			case MotionEvent.ACTION_POINTER_DOWN:
				Log.w(TAG, "Pointer down");
				// It secondary pointers, so obtain their ids and check circles
				pointerId = event.getPointerId(actionIndex);

				xTouch = (int) event.getX(actionIndex);
				yTouch = (int) event.getY(actionIndex);

				// check if we've touched inside some circle
				touchedCircle = obtainTouchedCircle(xTouch, yTouch);

				mCirclePointer.put(pointerId, touchedCircle);
				touchedCircle.centerX = xTouch;
				touchedCircle.centerY = yTouch;
				invalidate();
				handled = true;
				break;

			case MotionEvent.ACTION_MOVE:
				final int pointerCount = event.getPointerCount();

				Log.w(TAG, "Move");

				for (actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
					// Some pointer has moved, search it by pointer id
					pointerId = event.getPointerId(actionIndex);

					xTouch = (int) event.getX(actionIndex);
					yTouch = (int) event.getY(actionIndex);

					touchedCircle = mCirclePointer.get(pointerId);

					if (null != touchedCircle) {
						touchedCircle.centerX = xTouch;
						touchedCircle.centerY = yTouch;
					}
				}
				invalidate();
				handled = true;
				break;

			case MotionEvent.ACTION_UP:
				invalidate();
				handled = true;
				break;

			case MotionEvent.ACTION_POINTER_UP:
				// not general pointer was up
				pointerId = event.getPointerId(actionIndex);

				mCirclePointer.remove(pointerId);
				invalidate();
				handled = true;
				break;

			case MotionEvent.ACTION_CANCEL:
				handled = true;
				break;

			default:
				// do nothing
				break;
		}

		return super.onTouchEvent(event) || handled;
	}

	private DrawingVieww.CircleArea obtainTouchedCircle(final float xTouch, final float yTouch) {
		DrawingVieww.CircleArea touchedCircle = getTouchedCircle(xTouch, yTouch);

		if (null == touchedCircle) {
		}

		return touchedCircle;
	}
	private DrawingVieww.CircleArea getTouchedCircle(final float xTouch, final float yTouch) {
		DrawingVieww.CircleArea touched = null;

		for (DrawingVieww.CircleArea circle : mCircles) {
			if ((circle.centerX - xTouch) * (circle.centerX - xTouch) + (circle.centerY - yTouch) * (circle.centerY - yTouch) <= circle.radius * circle.radius) {
				touched = circle;
				break;
			}
		}

		return touched;
	}
	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mMeasuredRect = new Rect(0, 0, getMeasuredWidth(), getMeasuredHeight());
	}
	//------------------------------------------------------------------
	// Line
	//------------------------------------------------------------------

	private void onDrawLine(Canvas canvas) {

		float dx = Math.abs(mx - mStartX);
		float dy = Math.abs(my - mStartY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			canvas.drawLine(mStartX, mStartY, mx, my, mDrawPaint);
		}
	}
	@NonNull
	private Boolean onTouchEventLine(MotionEvent event) {
		mStartX = event.getX();
		mStartY = event.getY();

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				touch_start(mStartX, mStartY);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touch_move(mStartX, mStartY);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touch_up();
				invalidate();
				break;
		}

		return true;
	}
	private void touch_start(float x, float y) {
		if (isEraserActive) {
			mCurrentShape=2;
			mDrawPaint.setStrokeWidth(20);
			if(mBackgroundColor== ContextCompat.getColor(getContext(), android.R.color.transparent)){
				mDrawPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
			}else{
				mDrawPaint.setColor(mBackgroundColor);
			}
			mDrawPaint.setStrokeWidth(mStrokeWidth*3);
			isDrawing = true;
			mStartX = mx;
			mStartY = my;
			invalidate();
		}
		else {
			isDrawing = true;
			mCurrentShape=LINE;
			mDrawPaint.setColor(mPaintColor);
			mDrawPath.moveTo(x, y);
			mx = x;
			mx = y;
		}
        invalidate();
	}
	private void touch_move(float x, float y) {
		float dx = Math.abs(x - mx);
		float dy = Math.abs(y - my);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
		{
			mDrawPath.quadTo(mx, my, (x + mx)/2, (y + my)/2);
			mx = x;
			my = y;
		}
	}
	private void touch_up() {
		mDrawPath.lineTo(mx, my);
		mPaths.add(mDrawPath);
		mPaints.add(mDrawPaint);
		mDrawPath = new Path();
		initPaint();
	}

	//----Smooth Line---//
	public boolean onTouchEventSmoothLine(MotionEvent event) {
			mStartX = event.getX();
			mStartY = event.getY();
			switch (event.getAction())
			{
				case MotionEvent.ACTION_DOWN:
					if (isEraserActive) {
						mDrawPaint.setStrokeWidth(20);
						if(mBackgroundColor== ContextCompat.getColor(getContext(), android.R.color.transparent)){
							mDrawPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
						}else{
							mDrawPaint.setColor(mBackgroundColor);
						}
						mDrawPaint.setStrokeWidth(mStrokeWidth*3);
						mDrawPath.moveTo(mStartX, mStartY);
					}

					else {
						mDrawPaint.setColor(mPaintColor);
						mDrawPath.moveTo(mStartX, mStartY);
					}

					invalidate();
					break;
				case MotionEvent.ACTION_MOVE:
					mDrawPath.lineTo(mStartX, mStartY);
					invalidate();
					break;
				case MotionEvent.ACTION_UP:
					mDrawPath.lineTo(mStartX, mStartY);
					mPaths.add(mDrawPath);
					mPaints.add(mDrawPaint);
					mDrawPath = new Path();
					initPaint();
					invalidate();
					break;
				default:
					return false;
			}
			invalidate();
			return true;
		}
	//------------------------------------------------------------------
	// Triangle
	//------------------------------------------------------------------

	int countTouch =0;
	float basexTriangle =0;
	float baseyTriangle =0;

	private void onDrawTriangle(Canvas canvas){

		if (countTouch<3){
			canvas.drawLine(mStartX,mStartY,mx,my,mDrawPaint);
		}else if (countTouch==3){
			canvas.drawLine(mx,my,mStartX,mStartY,mDrawPaint);
			canvas.drawLine(mx,my,basexTriangle,baseyTriangle,mDrawPaint);
		}
	}
	private void onTouchEventTriangle(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
                if (isEraserActive) {
                    mCurrentShape=2;
                    mDrawPaint.setStrokeWidth(20);
                    if(mBackgroundColor== ContextCompat.getColor(getContext(), android.R.color.transparent)){
                        mDrawPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
                    }else{
                        mDrawPaint.setColor(mBackgroundColor);
                    }
                    mDrawPaint.setStrokeWidth(mStrokeWidth*3);
                    isDrawing = true;
                    mStartX = mx;
                    mStartY = my;
                    invalidate();
                }
                else {
                    mCurrentShape = TRIANGLE;
                    isDrawing = true;
                    countTouch++;
                    if (countTouch==1){
                        isDrawing = true;
                        mStartX = mx;
                        mStartY = my;
                    } else if (countTouch==3){
                        isDrawing = true;
                    }
                }
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				countTouch++;
				isDrawing = false;
				if (countTouch<3){
					basexTriangle=mx;
					baseyTriangle=my;
					mDrawCanvas.drawLine(mStartX,mStartY,mx,my,mDrawPaint);
				} else if (countTouch>=3){
					mDrawCanvas.drawLine(mx,my,mStartX,mStartY,mDrawPaint);
					mDrawCanvas.drawLine(mx,my,basexTriangle,baseyTriangle,mDrawPaint);
					countTouch =0;
				}
				invalidate();
				break;
		}
	}
    //------------------------------------------------------------------
    // Rectangle
    //------------------------------------------------------------------
	private void onTouchEventRectangle(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (isEraserActive) {
					mCurrentShape=2;
					mDrawPaint.setStrokeWidth(20);
					if(mBackgroundColor== ContextCompat.getColor(getContext(), android.R.color.transparent)){
						mDrawPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
					}else{
						mDrawPaint.setColor(mBackgroundColor);
					}
					mDrawPaint.setStrokeWidth(mStrokeWidth*3);
					isDrawing = true;
					mStartX = mx;
					mStartY = my;
					invalidate();
				}
				else {
					isDrawing = true;
					mCurrentShape=3;
					mDrawPaint.setColor(mPaintColor);
					mStartX = mx;
					mStartY = my;
				}

				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				isDrawing = false;
				float right = mStartX > mx ? mStartX : mx;
				float left = mStartX > mx ? mx : mStartX;
				float bottom = mStartY > my ? mStartY : my;
				float top = mStartY > my ? my : mStartY;
				mDrawPath.addRect(left, top , right, bottom, Path.Direction.CCW);
				mPaths.add(mDrawPath);
				mPaints.add(mDrawPaint);
				mDrawPath = new Path();
				initPaint();
				invalidate();
				break;
		}
		;
	}
    private void onDrawRectangle(Canvas canvas) {
        drawRectangle(canvas,mDrawPaint);
    }
    private void drawRectangle(Canvas canvas,Paint paint){
        float right = mStartX > mx ? mStartX : mx;
        float left = mStartX > mx ? mx : mStartX;
        float bottom = mStartY > my ? mStartY : my;
        float top = mStartY > my ? my : mStartY;
        canvas.drawRect(left, top , right, bottom, paint);
    }

    //------------------------------------------------------------------
    // Circle
    //------------------------------------------------------------------

    private void onDrawCircle(Canvas canvas){
        canvas.drawCircle(mStartX, mStartY, calculateRadius(mStartX, mStartY, mx, my), mDrawPaint);
    }
    private void onTouchEventCircle(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isEraserActive) {
                    mCurrentShape=2;
                    mDrawPaint.setStrokeWidth(20);
                    if(mBackgroundColor== ContextCompat.getColor(getContext(), android.R.color.transparent)){
                        mDrawPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
                    }else{
                        mDrawPaint.setColor(mBackgroundColor);
                    }
                    mDrawPaint.setStrokeWidth(mStrokeWidth*3);
                    isDrawing = true;
                    mStartX = mx;
                    mStartY = my;
                    invalidate();
                }
                else {
                    isDrawing = true;
                    mCurrentShape=CIRCLE;
					mDrawPaint.setColor(mPaintColor);
                    mStartX = mx;
                    mStartY = my;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrawing = false;
				//mDrawPath.addCircle(mStartX, mStartY, calculateRadius(mStartX,mStartY,mx,my), Path.Direction.CCW);
				DrawingVieww.CircleArea touchedCircle = getTouchedCircle(mStartX, mStartY);
				if (null == touchedCircle) {
					touchedCircle = new DrawingVieww.CircleArea(mStartX, mStartX, (int) calculateRadius(mStartX, mStartY, mx, my));
					Log.w(TAG, "Added circle " + touchedCircle);
					mCircles.add(touchedCircle);
				}
				mPaths.add(mDrawPath);
				mPaints.add(mDrawPaint);
				mDrawPath = new Path();
				initPaint();
                invalidate();
                break;
        }
    }
    protected float calculateRadius(float x1, float y1, float x2, float y2) {

        return (float) Math.sqrt(
                Math.pow(x1 - x2, 2) +
                        Math.pow(y1 - y2, 2)
        );
    }


    //------------------------------------------------------------------
	// Square
	//------------------------------------------------------------------
	private void onTouchEventSquare(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
                if (isEraserActive) {
                    mCurrentShape=2;
                    mDrawPaint.setStrokeWidth(20);
                    if(mBackgroundColor== ContextCompat.getColor(getContext(), android.R.color.transparent)){
                        mDrawPaint.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
                    }else{
                        mDrawPaint.setColor(mBackgroundColor);
                    }
                    mDrawPaint.setStrokeWidth(mStrokeWidth*3);
                    isDrawing = true;
                    mStartX = mx;
                    mStartY = my;
                    invalidate();
                }
                else {
                    isDrawing = true;
                    mCurrentShape=SQUARE;
					mDrawPaint.setColor(mPaintColor);
                    mStartX = mx;
                    mStartY = my;
                }
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				adjustSquare(mx, my);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				isDrawing = false;
				adjustSquare(mx, my);
				float right = mStartX > mx ? mStartX : mx;
				float left = mStartX > mx ? mx : mStartX;
				float bottom = mStartY > my ? mStartY : my;
				float top = mStartY > my ? my : mStartY;
				mDrawPath.addRect(left, top , right, bottom, Path.Direction.CCW);
				mPaths.add(mDrawPath);
				mPaints.add(mDrawPaint);
				mDrawPath = new Path();
				initPaint();
				invalidate();
				break;
		}
	}
	protected void adjustSquare(float x, float y) {
		float deltaX = Math.abs(mStartX - x);
		float deltaY = Math.abs(mStartY - y);

		float max = Math.max(deltaX, deltaY);

		mx = mStartX - x < 0 ? mStartX + max : mStartX - max;
		my = mStartY - y < 0 ? mStartY + max : mStartY - max;
	}

	public void clearCanvas()
	{
		mPaths.clear();
		mPaints.clear();
		mUndonePaths.clear();
		mUndonePaints.clear();
		mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		mCircles.clear();
		invalidate();
	}
	public void setPaintColor(int color)
	{
		mPaintColor = color;
		mDrawPaint.setColor(mPaintColor);
	}
	public void getPaintColor(int color)
	{
		mPaintColor = color;
		mDrawPaint.setColor(mPaintColor);
	}

	public void setPaintStrokeWidth(int strokeWidth)
	{
		mStrokeWidth = strokeWidth;
		mDrawPaint.setStrokeWidth(mStrokeWidth);
	}

	public void setBackgroundColor(int color)
	{
		mPaths.clear();
		mPaints.clear();
		mUndonePaths.clear();
		mBackgroundColor = color;
		mBackgroundPaint.setColor(mBackgroundColor);
		invalidate();
	}
	public void getBackgroundColor(){
		mBackgroundPaint.getColor();
	}

	public void undo()
	{
		if (mPaths.size() > 0)
		{
			mUndonePaths.add(mPaths.remove(mPaths.size() - 1));
			mUndonePaints.add(mPaints.remove(mPaints.size() - 1));
			invalidate();
		}
		if(mCircles.size()>0){

			circle.add(mCircles.remove(mCircles.size() - 1));
			invalidate();
		}
	}

	public void redo()
	{
		if (mUndonePaths.size() > 0)
		{
			mPaths.add(mUndonePaths.remove(mUndonePaths.size() - 1));
			mPaints.add(mUndonePaints.remove(mUndonePaints.size() - 1));
			invalidate();
		}
		if(circle.size()>0){
			mCircles.add(circle.remove(circle.size() - 1));
			invalidate();
		}
	}
	public void activateEraser()
	{
		isEraserActive = true;
	}

	public void deactivateEraser()
	{
		isEraserActive = false;
	}

	public boolean isEraserActive()
	{
		return isEraserActive;
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}
	/** Stores data about single circle */
	private static class CircleArea {
		int radius;
		int centerX;
		int centerY;

		CircleArea(float centerX, float centerY, int radius) {
			this.radius = radius;
			this.centerX = (int) centerX;
			this.centerY = (int) centerY;
		}

		@Override
		public String toString() {
			return "Circle[" + centerX + ", " + centerY + ", " + radius + "]";
		}
	}
}
