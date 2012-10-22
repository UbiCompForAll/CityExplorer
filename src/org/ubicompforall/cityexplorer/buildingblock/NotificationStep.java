package org.ubicompforall.cityexplorer.buildingblock;

import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;
import org.ubicompforall.cityexplorer.gui.NotificationMessage;
import org.ubicompforall.simplelanguage.runtime.AbstractStepInstance;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationStep extends AbstractStepInstance implements AndroidBuildingBlockInstance{
	// Create Android notification
	String ns = Context.NOTIFICATION_SERVICE;
	private Context context;

	@Override
	public void setContext(Context context) {
		this.context = context;
		debug(-1, "Context set to "+ context);
	}

	@Override
	public void execute() {
		
		// Get parameter for building block
		String msg = getStringPropertyValue ("msg");
		String title = getStringPropertyValue ("title");
		debug (2, "title/msg is "+ title+"/"+msg );

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService( ns );
	
		int icon = R.drawable.icon;
		CharSequence tickerText = title; // 1-second "ticker" notification in the top bar
		long when = System.currentTimeMillis();
	
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
	
		// Create Intent for pending Intent 
		Intent showIntent = new Intent( context, NotificationMessage.class );
		showIntent.putExtra(CityExplorer.EXTRA_MESSAGE, title+": "+msg );
		//startActivity( showIntent );

		// Create pending Intent
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showIntent, 0); 
		
		notification.setLatestEventInfo(context, title, msg, contentIntent );
		mNotificationManager.notify(999, notification);
	}//execute
	
	//Delegate debug function
	public void debug(int level, String str){
		CityExplorer.debug(level,str);
	}//debug

}//class NotificationStep
