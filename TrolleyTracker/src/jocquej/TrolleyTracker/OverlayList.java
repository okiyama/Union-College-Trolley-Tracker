package jocquej.TrolleyTracker;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Represents a list of OverlayItems that can be drawn on
 * to a MapView.
 * @author Julian Jocque
 * @version 6/29/2012
 *
 */
public class OverlayList extends ItemizedOverlay<OverlayItem> 
{
	private ArrayList<OverlayItem> mOverlays;
	private Context mContext;
	
	/**
	 * Default constructor from ItemizedOverlay
	 * @param defaultMarker Asset to use as default icon that is drawn over each item
	 */
	public OverlayList(Drawable defaultMarker) 
	{		
		super(defaultMarker);
		mOverlays = new ArrayList<OverlayItem>();
	}
	
	/**
	 * Constructor that allows for added Context
	 * @param defaultMarker Asset to use as default icon that is drawn over each item
	 * @param context Context to use, provided by Android, so you shouldn't have to worry about it.
	 */
	public OverlayList(Drawable defaultMarker, Context context) 
	{
		super(boundCenterBottom(defaultMarker));
		mOverlays = new ArrayList<OverlayItem>();
		mContext = context;
	}

	/**
	 * Deals with a user tapping an item
	 * @param index The index of the item that was tapped
	 */
	@Override
	protected boolean onTap(int index) 
	{
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}
	
	/**
	 * Adds an overlay to the list with default marker
	 * @param overlay The overlay to add to the list
	 */
	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}
	
	/**
	 * Adds an overlay to the list with given marker
	 * @param marker Images asset to use for this marker
	 * @param overlay OverlayItem we are adding to the list
	 */
	public void addMarkerOverlay(Drawable marker, OverlayItem overlay)
	{
		overlay.setMarker(marker);
		this.addOverlay(overlay);
	}
	
	/**
	 * Getter for the item at index i. Must be called createItem
	 * instead of getItem to Override existing method.
	 * @param i the index we want to get
	 */
	@Override
	protected OverlayItem createItem(int i) 
	{
		return mOverlays.get(i);
	}

	/**
	 * returns the size of the OverlayList
	 * @return The size of this OverlayList
	 */
	@Override
	public int size() 
	{
		return mOverlays.size();
	}
}
