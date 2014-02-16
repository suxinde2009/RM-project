package com.jan.rm.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.View;

import com.jan.rm.R;

public class CompassView extends BaseCompassView {

	private static final String TAG = "CompassView";

	private PointF startPoint;
	private PointF endPoint;
	
	private Path path;
	private Path degreePath;
	private Path degreeDiv30Path;
	
	private PorterDuffXfermode porterDuffMode;
	private boolean isXferModeEnable;

	private float degreeDistance;
	private float degreeDistanceRatio = 0.4F;
	private float degreeStringSize;
	private float directionDistance;
	private float directionDistanceRatio = 0.25F;
	private float directionStringSize;
	private float barOuterDistance;
	private float barOuterDistanceRatio = 0.35F;
	private float barInnerDistance;
	private float barInnerDistanceRatio = 0.30F;
	private float crossDistance;
	private float crossDistanceRatio = 0.13F;
	
	private float arrowSize;

	private String[] directionResources = new String[4];

	private Paint paint;
	private Rect textBound;

	public CompassView(Context context) {
		this(context, null);
	}

	public CompassView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public CompassView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		isXferModeEnable = true;
		porterDuffMode = new PorterDuffXfermode(PorterDuff.Mode.XOR);

		centerPoint = new Point();
		startPoint = new PointF();
		endPoint = new PointF();
		
		path = new Path();
		degreePath = new Path();
		degreeDiv30Path = new Path();

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0xFFFFFFFF);

		textBound = new Rect();

		directionResources[0] = context.getString(R.string.north);
		directionResources[1] = context.getString(R.string.east);
		directionResources[2] = context.getString(R.string.south);
		directionResources[3] = context.getString(R.string.west);

		degreeStringSize = context.getResources().getDimension(R.dimen.compass_view_degree_string_size);
		directionStringSize = context.getResources().getDimension(R.dimen.compass_view_direction_string_size);
		arrowSize = context.getResources().getDimension(R.dimen.compass_view_arrow_size);
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
			setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (width > height) {
			degreeDistance = height * degreeDistanceRatio;
			directionDistance = height * directionDistanceRatio;
			barOuterDistance = height * barOuterDistanceRatio;
			barInnerDistance = height * barInnerDistanceRatio;
			crossDistance = height * crossDistanceRatio;
		} else {
			degreeDistance = width * degreeDistanceRatio;
			directionDistance = width * directionDistanceRatio;
			barOuterDistance = width * barOuterDistanceRatio;
			barInnerDistance = width * barInnerDistanceRatio;
			crossDistance = width * crossDistanceRatio;
		}
		
		if(degreePath.isEmpty()){
			for (int i = 0; i < 360; i += 2) {
				double realAngle = getRadiansForDraw(angle + i);

				startPoint = centerRadiusPoint(centerPoint, realAngle, barOuterDistance);
				endPoint = centerRadiusPoint(centerPoint, realAngle, barInnerDistance);
				if(i % 30 == 0){
					degreeDiv30Path.moveTo(startPoint.x, startPoint.y);
					degreeDiv30Path.lineTo(endPoint.x, endPoint.y);
				}else{
					degreePath.moveTo(startPoint.x, startPoint.y);
					degreePath.lineTo(endPoint.x, endPoint.y);
				}
			}
			
			path.moveTo(centerPoint.x - arrowSize, centerPoint.y - barOuterDistance - 2);
			path.lineTo(centerPoint.x + arrowSize, centerPoint.y - barOuterDistance - 2);
			path.lineTo(centerPoint.x, centerPoint.y - barOuterDistance - arrowSize * 2);
		}

	}

	protected PointF centerRadiusPoint(Point center, double angle, double radius) {
		PointF p = new PointF();
		p.x = (float) (radius * Math.cos(angle) + center.x);
		p.y = (float) (radius * Math.sin(angle) + center.y);

		return p;
	}

	protected int distance(Point point1, Point point2) {
		int dx = point2.x - point1.x;
		int dy = point2.y - point1.y;

		return (int) Math.sqrt(dx * dx + dy * dy);
	}

	protected double getRadiansForDraw(float angle) {
		if (angle >= 0 && angle <= 270) {
			angle -= 90;
		} else {
			angle -= 450;
		}

		return Math.toRadians(angle);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setXferModeEnabled(boolean enable){
		isXferModeEnable = enable;
		if(!isXferModeEnable){
			porterDuffMode = null;
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) setLayerType(View.LAYER_TYPE_HARDWARE, null); 
		}
	}

	@Override
	public void onThreadDraw(Canvas canvas) {
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(0xFFFFFFFF);
		paint.setStrokeWidth(6);
		canvas.drawLine(centerPoint.x, centerPoint.y - barInnerDistance, centerPoint.x, centerPoint.y - barInnerDistance - crossDistance, paint);
		if(isXferModeEnable) paint.setXfermode(porterDuffMode);
		
		
		paint.setStyle(Paint.Style.FILL);
		for(int i = 0; i < 12; i++){
			int target = i * 30;
			
			double realAngle = getRadiansForDraw(angle + target);
			
			paint.setTextSize(degreeStringSize);
			paint.getTextBounds(target + "", 0, (target + "").length(), textBound);
			startPoint = centerRadiusPoint(centerPoint, realAngle, degreeDistance);

			canvas.drawText(target + "", startPoint.x - textBound.width() / 2, startPoint.y + textBound.height() / 2, paint);

			if (i == 0 || i % 3 == 0) {
				paint.setTextSize(directionStringSize);
				paint.getTextBounds(directionResources[i / 3], 0, directionResources[i / 3].length(), textBound);
				startPoint = centerRadiusPoint(centerPoint, realAngle, directionDistance);
				canvas.drawText(directionResources[i / 3], startPoint.x - textBound.width() / 2, startPoint.y + textBound.height() / 2, paint);
			}
			
		}

		paint.setStrokeWidth(1);
		canvas.drawLine(centerPoint.x - crossDistance, centerPoint.y, centerPoint.x + crossDistance, centerPoint.y, paint);
		canvas.drawLine(centerPoint.x, centerPoint.y - crossDistance, centerPoint.x, centerPoint.y + crossDistance, paint);
		
		canvas.save();
		canvas.rotate(angle, centerPoint.x, centerPoint.y);
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(0xFFFFFFFF);
		paint.setStrokeWidth(1);
		
		canvas.drawPath(degreePath, paint);
		paint.setStrokeWidth(3);
		canvas.drawPath(degreeDiv30Path, paint);
		
		paint.setXfermode(null);
		
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(0xFFFF0000);
		
		canvas.drawPath(path, paint);
		canvas.restore();

	}

}