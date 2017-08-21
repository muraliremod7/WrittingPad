package com.indianservers.writtingpad.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;

public class DrawingVieww extends View
{
	public static final int LINE = 1;
	public static final int RECTANGLE = 3;
	public static final int SQUARE = 4;
	public static final int CIRCLE = 5;
	public static final int TRIANGLE = 6;
	public static final int SMOOTHLINE = 2;
	public int mCurrentShape;
	protected float mStartX,mStartY;
	protected float mx, my;
	protected boolean isDrawing = false;
	public static Paint mPaintErase = new Paint();
	public static Paint mPaintBitmap;
	private Path mDrawPath;
	private Paint mBackgroundPaint;
	private Paint mDrawPaint;
	public  Canvas mDrawCanvas;
	private Bitmap mCanvasBitmap;
	private int width, height;
	private ArrayList<Path> mPaths = new ArrayList<>();
	private ArrayList<Paint> mPaints = new ArrayList<>();
	private ArrayList<Path> mUndonePaths = new ArrayList<>();
	private ArrayList<Paint> mUndonePaints = new ArrayList<>();
	private ArrayList<Pair<Path, Paint>> paths = new ArrayList<Pair<Path, Paint>>();
	// Set default values
	private int mBackgroundColor = ContextCompat.getColor(getContext(), android.R.color.white);
	private int mPaintColor = ContextCompat.getColor(getContext(), android.R.color.black);
	private int mStrokeWidth = 5;
	private boolean isEraserActive = false;

	private static final float TOUCH_TOLERANCE = 4;
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
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		drawBackground(canvas);
		drawPaths(canvas);
		for (Pair<Path, Paint> p : paths) {
			canvas.drawPath(p.first, p.second);
		}
		canvas.drawPath(mDrawPath,mDrawPaint);
		canvas.drawBitmap(mCanvasBitmap, 0, 0, mDrawPaint);
		if (isDrawing){
			switch (mCurrentShape) {
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
	public void drawBackground(Canvas canvas)
	{
		mBackgroundPaint.setColor(mBackgroundColor);
		mBackgroundPaint.setStyle(Paint.Style.FILL);
		canvas.drawRect(0, 0, this.getWidth(), this.getHeight(), mBackgroundPaint);
	}

	private void drawPaths(Canvas canvas)
	{
		int i = 0;
		for (Path p : mPaths)
		{
			canvas.drawPath(p, mPaints.get(i));
			i++;
		}
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
	private void onTouchEventLine(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
                if (isEraserActive) {
					isDrawing = true;
                }
                else {
                    mCurrentShape = LINE;
                    isDrawing = true;
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
				mDrawCanvas.drawLine(mStartX, mStartY, mx, my, mDrawPaint);
				invalidate();
				break;
		}
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
				mDrawPath.addCircle(mStartX, mStartY, calculateRadius(mStartX,mStartY,mx,my), Path.Direction.CCW);
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


	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = w;
		height = h;
		mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mDrawCanvas = new Canvas(mCanvasBitmap);
	}
	public void clearCanvas()
	{
		mPaths.clear();
		mPaints.clear();
		mUndonePaths.clear();
		mUndonePaints.clear();
		setPaintColor(Color.BLACK);
		mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

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
	}

	public void redo()
	{
		if (mUndonePaths.size() > 0)
		{
			mPaths.add(mUndonePaths.remove(mUndonePaths.size() - 1));
			mPaints.add(mUndonePaints.remove(mUndonePaints.size() - 1));
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
}
