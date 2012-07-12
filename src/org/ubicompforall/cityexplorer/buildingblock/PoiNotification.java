/**
 * @contributor(s): Rune Sætre (NTNU)
 *
 * Copyright (C) 2011-2012 UbiCompForAll Consortium (SINTEF, NTNU)
 * for the UbiCompForAll project
 *
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 *
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */

package org.ubicompforall.cityexplorer.buildingblock;

import java.util.Map;

import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;
import org.ubicompforall.simplelanguage.runtime.AbstractStepInstance;
import org.ubicompforall.simplelanguage.runtime.TaskInstance;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;

public class PoiNotification extends AbstractStepInstance implements AndroidBuildingBlockInstance {
	Context context;

	@Override
	public int execute(TaskInstance task, Map<String, Object> parameters) {
		CityExplorer.debug(-1, "Show notification" );
		//Context context = this;
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService( ns );

		int icon = R.drawable.ic_launcher;
		CharSequence tickerText = "Arriving at Poi";
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		CharSequence contentTitle = "You have arrived at POI";
		CharSequence contentText = "Get info about "+this.getPropertyValue("poiName", parameters);

		Intent showIntent = new Intent(context, org.ubicompforall.cityexplorer.gui.CalendarActivity.class);
		Time now = new Time();
		now.setToNow();
		showIntent.putExtra( "time", now.toString() );
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showIntent, 0); 
	    //To make an Intent with no action, use this instead of Intent:
		//contentIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, new Intent(), 0);
		
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent );
		mNotificationManager.notify(999, notification);
		return 0;
	}//execute

	@Override
	public void setContext(Context context) {
		this.context = context;
	}//setContext

}//class PoiNotification
