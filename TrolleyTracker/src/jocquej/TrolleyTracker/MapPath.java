package jocquej.TrolleyTracker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

/**
 * A path that should be added to the Overlays of a MapView.
 * For example, a route to follow.
 * @author Julian Jocque
 * @version 08/17/2012
 */
public class MapPath extends Overlay
{
	private OverlayList points;
	private Paint brush;
	private boolean loops;
	
	/**
	 * Constructor for the Path. OverlayList must be in order as
	 * the lines of the path will be drawn from one point to the next in
	 * order.
	 * @param pointsToUse Ordered list of points to use for the path.
	 * @param typeOfLine Properties the lines should have, expressed by a 
	 * Paint object.
	 * @param loop Whether or not the path should loop on itself
	 */
	public MapPath(OverlayList pointsToUse, Paint typeOfLine, boolean shouldLoop)
	{
		brush = typeOfLine;
		points = pointsToUse;
		loops = shouldLoop;
	}
	
	/**
	 * Draws the path to the Map. This method is automatically called when
	 * the Map that it is added to is drawn to the screen.
	 * @param canvas The Android canvas, taken care of internally
	 * @param toDrawOn The mapview we are drawing on
	 * @param shadow Whether or not we want a shadow on the path
	 */
	public void draw(Canvas canvas, MapView toDrawOn, boolean shadow)
	{
		super.draw(canvas, toDrawOn, shadow);
		OverlayItem item1;
		OverlayItem item2;
		
		//Draws a line between all of the points in the OverlayList
		for (int i = 0; i < points.size()-1; i++)
		{
			item1 = points.createItem(i);
			item2 = points.createItem(i+1);
			
			drawLine(item1, item2, toDrawOn, canvas);
		}
		//If the path should loop we draw a line from the end to the start
		if (loops)
		{
			drawLine(points.createItem(points.size()-1), points.createItem(0), toDrawOn, canvas);
		}
	}
	
	/**
	 * Draws a line on the map between two OverlayItems
	 * @param p1 First point in the line
	 * @param p2 Second point in the line
	 * @param toDrawOn The MapView we are drawing to
	 * @param canvas The canvas we are using to draw
	 */
	private void drawLine(OverlayItem p1, OverlayItem p2, MapView toDrawOn, Canvas canvas)
	{
		//Because overlay item can only give us a GeoPoint and we need to Points to draw on the map
		//we have to do some conversions.
		GeoPoint gP1;
		GeoPoint gP2;
		Point point1;
		Point point2;
		
		//Gets the GeoPoints of our OverlayItems
		gP1 = p1.getPoint();
		gP2 = p2.getPoint();
		
		point1 = new Point();
		point2 = new Point();

		Path path = new Path();
		
		//Converts them to points on the map's Projection
		Projection proj = toDrawOn.getProjection();
		proj.toPixels(gP1,point1);
		proj.toPixels(gP2,point2);
		
		//Makes the line that will be drawn on to the map
		path.moveTo(point2.x,point2.y);
		path.lineTo(point1.x,point1.y);
		
		//Draws the line on to the Canvas
		canvas.drawPath(path, this.brush);
	}
}
