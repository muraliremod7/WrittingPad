package com.indianservers.writingpad.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingVieww extends View
{
	private Path mDrawPath;
	private Paint mBackgroundPaint;
	private Paint mDrawPaint;
	private Canvas mDrawCanvas;
	private Bitmap mCanvasBitmap;
	private float mX, mY;
	private ArrayList<Path> mPaths = new ArrayList<>();
	private ArrayList<Paint> mPaints = new ArrayList<>();
	private ArrayList<Path> mUndonePaths = new ArrayList<>();
	private ArrayList<Paint> mUndonePaints = new ArrayList<>();
	private ArrayList<Pair<Path, Paint>> paths = new ArrayList<Pair<Path, Paint>>();
	// Set default values
	private int mBackgroundColor = 0xFFFFFFFF;
	private int mPaintColor = 0xFF660000;
	private int mStrokeWidth = 10;
	private boolean isEraserActive = false;
	private static final float TOUCH_TOLERANCE = 4;
	public DrawingVieww(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
	public DrawingVieww(Context context){
		super(context);


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

	private void drawBackground(Canvas canvas)
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

	@Override
	protected void onDraw(Canvas canvas)
	{
		drawBackground(canvas);
		drawPaths(canvas);
		for (Pair<Path, Paint> p : paths) {
			canvas.drawPath(p.first, p.second);
		}
		canvas.drawPath(mDrawPath, mDrawPaint);
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

		mDrawCanvas = new Canvas(mCanvasBitmap);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		float touchX = event.getX();
		float touchY = event.getY();

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				if (isEraserActive) {
					mDrawPaint.setStrokeWidth(20);
					mDrawPaint.setColor(mBackgroundColor);
					mDrawPaint.setStrokeWidth(mStrokeWidth+mStrokeWidth+mStrokeWidth);
					mDrawPath.moveTo(touchX, touchY);
				} else {
					mDrawPaint.setColor(mPaintColor);
					mDrawPath.moveTo(touchX, touchY);

				}
				invalidate();
				//mDrawPath.addCircle(touchX, touchY, mStrokeWidth/10, Path.Direction.CW);
				break;
			case MotionEvent.ACTION_MOVE:
				mDrawPath.lineTo(touchX, touchY);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				mDrawPath.lineTo(touchX, touchY);
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

	public void clearCanvas()
	{
		mPaths.clear();
		mPaints.clear();
		mUndonePaths.clear();
		mUndonePaints.clear();
		mDrawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
		invalidate();
	}

	public void setPaintColor(int color)
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

	public void eraser(){
		mDrawPaint.setColor(Color.GREEN);
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
