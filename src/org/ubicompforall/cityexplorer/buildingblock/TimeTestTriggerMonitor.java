/**
 * @contributor(s): Jacqueline Floch (SINTEF)
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

import java.util.HashMap;
import java.util.Map;

//import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.simplelanguage.Task;
import org.ubicompforall.simplelanguage.runtime.TaskTrigger;
import org.ubicompforall.simplelanguage.runtime.TriggerMonitor;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

/**
 * @description: 	Trigger monitor that generates a trigger event every X minutes.
 * 					(X i specified by the user in the composition model)
 */

public class TimeTestTriggerMonitor extends BroadcastReceiver implements TriggerMonitor, AndroidBuildingBlockInstance {
	Context context;			//Context of the activity executing the trigger monitor
	TaskTrigger taskTrigger;
	Task task;

	private Integer recurrenceTime;		// Recurrence time for the trigger event 
	private Integer elapsedTime = 0;	// Elapsed time since last clock broadcasted tick
										// A tick is broadcast every minute

	@Override
	public void setContext(Context context) {
		this.context = context;
	}//AndroidBuildingBlockInstance.setContext

	
	/***
	 * task is the whole composition
	 * taskTrigger is for something completely different: 
	 */
	@Override
	public void startMonitoring( Task task, TaskTrigger taskTrigger ) {
		this.taskTrigger = taskTrigger;
		this.task = task;

		//TODO: retrieve recurrence time from composition model
		recurrenceTime = 2;
		
		// Check that the time is >= 0
		
		Toast.makeText(context, "Timer is now started with repeat"+ recurrenceTime.toString() + task.getName(), Toast.LENGTH_LONG).show();

		IntentFilter s_intentFilter = new IntentFilter();
		s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
		
		context.registerReceiver(this, s_intentFilter);
		

	}//TriggerMonitor.startMonitoring

	
	@Override
	public void stopMonitoring() {
		context.unregisterReceiver(this);
	}//TriggerMonitor.stopMonitoring
	 

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		elapsedTime++;
		if (elapsedTime == recurrenceTime) {
			Map<String, Object> parameterMap = new HashMap<String, Object>();
			taskTrigger.invokeTask(task, parameterMap);
		}
		
	}
	
//	public void debug(int level, String str){
//		CityExplorer.debug(level,str);
//	}
	
}//class PoiTrigger
