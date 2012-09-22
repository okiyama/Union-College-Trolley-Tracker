package jocquej.TrolleyTracker;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import jocquej.TrolleyTracker.R;

/**
 * Main class for the Union College Trolley Tracker App
 * Displays a map of Union College with the trolley at its current position.
 * Also displays the trolleys path.
 * Check trolley.union.edu for more info on the project
 * @author Julian Jocque
 * @version 8/21/12
 */

/* TODO: Include a menu with refresh(possible?) and info on app/trolley/site
 * 6. Clean up trolley adding, make more OO
 * 5. If it is not the times that the trolley runs pop up a warning, have a trolleyIsRunning() method
 * 4. Make testing class, remove testing code before putting on github
 * 1. Use ASyncTask to add a loading screen on app startup for when the MapView is loading.
 * 2. Add current location of the user to the map. This would require at least
 * 		a refresh button, but ideally the map would just refresh itself every 5-30 seconds, with an optional
 * 		refresh now button at the bottom. Don't use the menu button.
 * 3. Check to make sure the maps API key is good, not a debugging one. Remove my api key from the Github upload.
 * 1 1/2. Clean up old loading screen code
 * 
 * Future feature: "Where will the trolley be in X minutes/at this time"
 * How to achieve it:
 * Setup: Ping the trolley once per minute as it goes through its path. Would be better to be
 * on it while it does this to avoid any anomolies.
 * Once we have this data:
 * When the user asks for 3 minutes ahead all we do is find the point out
 * of these points nearest to current trolley position, then go 3 data points
 * ahead and draw that to the map.
 * This would all go in a new class.
 */

public class TrolleyTrackerActivity extends MapActivity {
	public static final int UNION_X_CENTER_MICRODEGREES = 42818332;
	public static final int UNION_Y_CENTER_MICRODEGREES = -73931787;
	public static final String LOG_TAG = "TrolleyTrackerActivity";
	public static final String SITE_URL = "http://trolley.union.edu/app/stats.php";
	
	private static final boolean DEBUG = false;
	//This is an array of x, y points for the path of the trolley.
	private static final int[][] trolleyPathPoints = {{42820710, -73934040}, {42821160, -73933850}, {42820740, -73931480},
		{42820650, -73931450}, {42819300, -73933710}, {42817090, -73935190}, {42816470, -73933470}, {42817470, -73932800},
		{42817560, -73933100}, {42818580, -73932400}, {42818580, -73931950}, {42819470, -73931350}, {42819690, -73930850},
		{42819650, -73930150}, {42819600, -73929930}, {42820340, -73929430}, {42819880, -73927040}, {42819560, -73927030},
		{42819200, -73926720}, {42818910, -73926640}, {42818620, -73926880}, {42818300, -73927530}, {42817950, -73927840},
		{42818030, -73928140}, {42818240, -73928040}, {42818310, -73927610}, {42818490, -73927170}, {42818770, -73926730},
		{42819040, -73926690}, {42819290, -73926870}, {42819550, -73927130}, {42819850, -73927070}, {42819890, -73926820},
		{42820100, -73923310}, {42820040, -73922550}, {42819180, -73922730}, {42818590, -73922990}, {42817790, -73923520},
		{42817170, -73924110}, {42816620, -73924650}, {42815930, -73925090}, {42815540, -73925300}, {42815340, -73925750},
		{42815150, -73927350}, {42815530, -73927370}, {42815690, -73927890}, {42815530, -73928110}, {42815370, -73929350},
		{42815220, -73930110}, {42815500, -73931080}, {42816410, -73930440}, {42816510, -73930300}, {42816590, -73930410},
		{42816520, -73930540}, {42816390, -73930480}, {42814690, -73931720}, {42815090, -73932970}, {42815480, -73934510},
		{42816030, -73936740}, {42816160, -73938880}, {42818970, -73937210}, {42820690, -73936120}, {42821100, -73936310},
		{42821460, -73935590}, {42821200, -73934190}, {42820820, -73934690}, {42820730, -73934050}};
	
	private CustomMapView mapView;
	private GeoPoint center;
	private ProgressDialog loadingDialog;
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.map);
	    
	    //loadingDialog = ProgressDialog.show(this, "Loading...", "Please wait");
	    
	    mapView = (CustomMapView) findViewById(R.id.mapview);
	    center = new GeoPoint(UNION_X_CENTER_MICRODEGREES, UNION_Y_CENTER_MICRODEGREES);
	    
	    setDefaultMapSettings();
	    
		addTrolleyPath();
		
		addTrolley();

	    mapView.invalidate(); //Recommended by Android to be called after changing Mapview Overlays
	    
		if (DEBUG)
		{
			runTests();
		}
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		//loadingDialog.dismiss();
	}

	/**
	 * Runs tests on some methods
	 * Will be moved to a dedicated testing class in the future
	 */
	private void runTests()
	{
		testFailMessage();
	}
	
	/**
	 * Tests the failMessage method
	 * All these run at once, should have a way to have one button wait for the next to finish
	 */
	private void testFailMessage()
	{
		failMessage("The app will now close", true);
		failMessage("Close the app?", false);
		failMessage(null, true);
		failMessage(null, false);
	}
	
	/**
	 * Handles a user double tapping to zoom in on the Map
	 */
	
	/**
	 * Sets the maps settings so they are correct for the trolley's route
	 */
	private void setDefaultMapSettings()
	{
		MapController mapController = mapView.getController();
		mapController.setCenter(center);
		mapController.setZoom(15);
		mapView.setBuiltInZoomControls(true);
	}
	
	/**
	 * Makes a MapPath out of the trolley's path which gets added to the overlays.
	 * This in turn makes the MapPath get drawn to the MapView.
	 * @throws IOException If we don't find TrolleyPathPoints.txt
	 */
	private void addTrolleyPath()
	{
		Paint brush = new Paint();
		setDefaults(brush);
		//To get our assets we "navigate" to the resources folder then to assets then to the proper file
		Drawable blankImage = this.getResources().getDrawable(R.drawable.blank);
		
		List<Overlay> mapOverlays = mapView.getOverlays();
		//We use a blank image here because we don't want the vertices of the path to have markers
		OverlayList trolleyPoints = new OverlayList(blankImage, this); 
		
		
		int currentX;
		int currentY;
		GeoPoint toAdd;
		
		//Here we read all of the points for the trolleys path from a txt file
		//and add them all as Geopoints to trolleyPoints.
		for (int i=0; i < trolleyPathPoints.length; i++)
		{
			currentX = trolleyPathPoints[i][0];
			Log.v(TrolleyTrackerActivity.LOG_TAG,"Current X is: " + currentX);

			currentY = trolleyPathPoints[i][1];
			Log.v(TrolleyTrackerActivity.LOG_TAG,"Current Y is: " + currentY);
			
			toAdd = new GeoPoint(currentX, currentY);
			trolleyPoints.addOverlay(new OverlayItem(toAdd, "", "")); //empty string so the overlays don't have a pop-up
		}
		
		//Now that we have an OverlayList with all of our points we can make a MapPath
		//out of that and add it to the MapOverlays
		mapOverlays.add(new MapPath(trolleyPoints, brush, true));
	}
	
	/**
	 * Sets the default settings for the paint of the path.
	 * Just here for easy modification
	 * @param paint The paint we are setting defaults for
	 */
	private void setDefaults(Paint paint) 
	{
		paint.setDither(true);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(3);
	}

	/**
	 * Adds the trolley at its current location to the map
	 */
	private void addTrolley()
	{
		Drawable trolleyIcon = this.getResources().getDrawable(R.drawable.ic_trolley_marker);
		OverlayList trolleyList = new OverlayList(trolleyIcon, this);
		
		int trolleyX;
		int trolleyY;
		String xStat = null;
		String yStat = null;
		String trolleyTime;
		GeoPoint trolley;
		org.jsoup.nodes.Document doc = null;
		
		//We connect to a remote webpage that has the current trolley stats on it
		//It's a bit messy, but there are not many better options because Android
		//cannot directly connect to a mysql server
		try {
			doc = Jsoup.connect(SITE_URL).get();
			Log.d(LOG_TAG, "Connected to site");
		} catch (IOException e) {
			failMessage("Could not connect to server. Press okay to exit.", true);
			Log.e(LOG_TAG,"Failed to connect to server");
			e.printStackTrace();
		}
		if (doc == null)
		{ 
			//Couldn't connect to server, so we tell user and close
			Log.e(LOG_TAG,"doc was null");
			failMessage("Could not connect to server. Most likely you don't have an internet connection. Press okay to exit.", true);
		}
		else
		{
			//This gets us the x and y co-ordinates from our stats page as Strings of floats and in degrees. 
			//We need them in ints and microdegrees.
			xStat = Jsoup.parse((((Element) doc).select("div.lat").toString())).text();
			yStat = Jsoup.parse((((Element) doc).select("div.lng").toString())).text();
			//Log.d(LOG_TAG, "yStat, string in degrees: " + yStat);
	
			Float xFloat = Float.parseFloat(xStat) * 1000000; //This gets x in microdegrees
			Float yFloat = Float.parseFloat(yStat) * 1000000; //Now we have y in microdegrees
			//Log.d(LOG_TAG, "yFloat, microdegrees: " + yFloat);
			
			trolleyX = Math.round(xFloat); //Turns our Float in to an Int
			trolleyY = Math.round(yFloat);
			//Log.d(LOG_TAG, "TrolleyX is: " + trolleyX + " TrolleyY is: " + trolleyY);
			
			//Gets the time from our stats page. Can keep it as a string.
			trolleyTime = Jsoup.parse(((Element) doc).select("div.time").toString()).text();
			trolley = new GeoPoint(trolleyX, trolleyY);
			
			trolleyList.addOverlay(new OverlayItem(trolley, "Last updated", trolleyTime));
			List<Overlay> mapOverlays = mapView.getOverlays();
			mapOverlays.add(trolleyList);
		}
	}

	/**
	 * Displays an error message to the user.
	 * @param toDisplay String to show them
	 * @param closeApp Whether or not the message should always close the app. If true there will only be
	 * one button shown that kill the app. If false there will be one button with "Yes" to continue, and one button
	 * with "No" to kill the app.
	 */
	private void failMessage(String toDisplay, boolean closeApp)
	{
		
		AlertDialog.Builder builder = new AlertDialog.Builder(TrolleyTrackerActivity.this);
		DialogInterface.OnClickListener closeAppButton = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				TrolleyTrackerActivity.this.finish();
			}
		};
		
		if (closeApp)
		{
			builder.setMessage(toDisplay)
			       .setCancelable(false)
			       .setPositiveButton("Okay", closeAppButton);
		}
		else
		{
			builder.setMessage(toDisplay)
		       .setCancelable(true)
		       .setNegativeButton("No", closeAppButton)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		}
		
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Simply required because we are extending MapActivity, unused
	 */
	@Override
	protected boolean isRouteDisplayed() 
	{
		return false;
	}
}