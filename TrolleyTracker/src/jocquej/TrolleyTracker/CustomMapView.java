package jocquej.TrolleyTracker;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.maps.MapView;

/**
 * MapView class customized to have additional features.
 * Currently it has added support for double tap zooming.
 * @author Julian Jocque
 * @version 08/21/12
 *
 */
public class CustomMapView extends MapView
{
	private long lastTouched = -1;
	public static final String LOG_TAG = "CustomMapView";
	
	public CustomMapView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	/**
	 * Handles users double tapping by zooming the MapView in one level
	 * @param MotionEvent ev The most recent MotionEvent that occurred.
	 * @return Whether or not the given MotionEvent was consumed
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if (ev.getAction() == MotionEvent.ACTION_DOWN)
		{
			//The tapped once, record what time it is
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastTouched < 250)
			{
				//If it is close enough to the last time they double tapped
				this.getController().zoomInFixing((int) ev.getX(), (int) ev.getY());
				lastTouched = -1;
			}
			else
			{
				//Too slow to count as a double tap
				lastTouched = currentTime;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

}
