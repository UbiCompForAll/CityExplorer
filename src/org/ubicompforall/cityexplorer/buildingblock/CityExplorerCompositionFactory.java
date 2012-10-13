/**
 * @contributor(s): Erlend Stav (SINTEF), Jacqueline Floch (SINTEF)
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

import org.ubicompforall.simplelanguage.BuildingBlock;
import org.ubicompforall.simplelanguage.runtime.BuildingBlockInstance;
import org.ubicompforall.simplelanguage.runtime.MapBasedBuildingBlockFactory;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockFactory;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.content.Context;

public class CityExplorerCompositionFactory extends MapBasedBuildingBlockFactory implements AndroidBuildingBlockFactory {
	Context context;
	
	@Override
	public BuildingBlockInstance createBuildingBlock(BuildingBlock buildingBlock) {
		BuildingBlockInstance inst = super.createBuildingBlock(buildingBlock);
		if (inst instanceof AndroidBuildingBlockInstance)
			((AndroidBuildingBlockInstance)inst).setContext(context);
		return inst;
	}

	
	private static final Map<String,Class<? extends BuildingBlockInstance>> MAP = createMap();
	
	private static Map<String,Class<? extends BuildingBlockInstance>> createMap() {
		Map<String, Class<? extends BuildingBlockInstance>> classMap = new HashMap<String,Class<? extends BuildingBlockInstance>>();
		
		// Trigger monitors
		classMap.put("ArrivingAtSomePoI", SomePoiTriggerMonitor.class);
		classMap.put("ArrivingAtSelectedPoI", SelectedPoiTriggerMonitor.class);
		classMap.put("TimeTestTrigger", TimeTestTriggerMonitor.class);
		classMap.put("PoiTimeTestTrigger", PoiTimeTestTriggerMonitor.class);
		
		// Steps
		classMap.put("NotifyLocation", PoiNotificationStep.class);
		classMap.put("NotifyMsg", NotificationStep.class);
		classMap.put("GetBusTimeStep", BusTimeStep.class);

		return classMap;
	}
	
	public CityExplorerCompositionFactory() {
		super(MAP);
	}

	@Override
	public void setContext(Context context) {
		this.context = context;		
	}
	

}
