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

//import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.simplelanguage.BuildingBlock;
import org.ubicompforall.simplelanguage.Task;
import org.ubicompforall.simplelanguage.runtime.BuildingBlockInstanceHelper;
import org.ubicompforall.simplelanguage.runtime.TaskInvoker;
import org.ubicompforall.simplelanguage.runtime.TriggerMonitor;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;
import org.ubicompforall.simplelanguage.DomainObjectReference;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

/**
 * @description: 	Trigger monitor that generates a trigger event every X minutes.
 * 					(X i specified by the user in the composition model)
 */



public class PoiTimeTestTriggerMonitor extends BroadcastReceiver implements TriggerMonitor, AndroidBuildingBlockInstance {
	
	

	// Required by UbiCompRun
	TaskInvoker taskInvoker;
	Task task;
	BuildingBlockInstanceHelper helper;

	Context context;					//Context of the activity executing the trigger monitor

	private static final Integer DEFAULT_RECURRENCE_TIME = 1; // Time unit = minute
	private Integer recurrenceTime;		// RecurrTaskInvokerence time for the trigger event 
	private Integer elapsedTime;		// Elapsed time since last clock broadcasted tick
										// A tick is broadcast every minute

	private static final String DEFAULT_POI = "Credo";
	private String poiName;

//	static IntentFilter s_intentFilter;
//
//	static {
//		s_intentFilter = new IntentFilter();
//		s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
//	}
	
	@Override
	public void setContext(Context context) {
		this.context = context;
	}//AndroidBuildingBlockInstance.setContext

	
	/***
	 * task is the whole composition
	 * taskTrigger is for something completely different: 
	 */
	@Override
	public void startMonitoring( Task task, TaskInvoker taskInvoker ) {
		this.taskInvoker = taskInvoker;
		this.task = task;

		if ( helper.getStringPropertyValue("recurrenceTime") == null ){
			recurrenceTime = DEFAULT_RECURRENCE_TIME;
		}else{
			recurrenceTime = Integer.parseInt( helper.getStringPropertyValue("recurrenceTime") );
		}

		
		DomainObjectReference poiRef = helper.getDomainObjectReference("PoI");
		if (poiRef != null) {
			poiName = poiRef.getDisplayText();
			if (poiName == null) {
				poiName = DEFAULT_POI;
			}
		} else {
			poiName = DEFAULT_POI;
		}
		
		
//		CityExplorer.debug(0, "Show notification "+ poiRef.getDisplayText() );

// When using strings:		
//		if ( helper.getStringPropertyValue("poiNameIn") == null ){
//			poiName = DEFAULT_POI;
//			
//		}else{
//			poiName = helper.getStringPropertyValue("poiNameIn");
//		}

		debug(1, "recurrenceTime is "+ recurrenceTime );
		debug(1, "poiName is "+ poiName );
		
		// Check that the time is > 0
		if (recurrenceTime > 0) {
			elapsedTime = recurrenceTime - 1; // Do not wait too long the first time!
		
			Toast.makeText(context, "Timer is now started with repeat"+ recurrenceTime.toString() + task.getName(), Toast.LENGTH_LONG).show();

			IntentFilter s_intentFilter = new IntentFilter();
			s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
			
			context.registerReceiver(this, s_intentFilter);
		}
		
		//Test it
		onReceive(context, null);
	}//TriggerMonitor.startMonitoring

	
	@Override
	public void stopMonitoring() {
		if (recurrenceTime > 0) {
			context.unregisterReceiver(this);
		}
	}//TriggerMonitor.stopMonitoring
	
	@Override
	public void setBuildingBlock(BuildingBlock buildingBlock) {
		helper = new BuildingBlockInstanceHelper(buildingBlock);		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		debug(-1, "Check Minutes! Intent is "+intent );
		if ( elapsedTime == null  ||  elapsedTime > recurrenceTime) {
			debug(-1, "Invoking the next task");
			elapsedTime = 0;	// reset time count
			
			// TODO: this property is now set because no support to accessing properties set by the user
			helper.setPropertyValue("poiNameOut", poiName);
			
			taskInvoker.invokeTask(task, helper.createTaskParameterMap());
		}else{
			elapsedTime++;
		}
	}//onReceive

	public void debug(int level, String str){
		CityExplorer.debug(level,str);
	}
	
}//class PoiTrigger
