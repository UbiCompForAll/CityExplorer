/**
 * @contributor(s): Erlend Stav (SINTEF), Rune Sætre (NTNU), Jacqueline Floch (SINTEF)
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
package org.ubicompforall.CityExplorer.buildingblock;

import java.util.HashMap;
import java.util.Map;

import org.ubicompforall.simplelanguage.runtime.BuildingBlockInstance;
import org.ubicompforall.simplelanguage.runtime.MapBasedBuildingBlockFactory;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockFactory;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.content.Context;

public class CompositionFactory extends MapBasedBuildingBlockFactory implements AndroidBuildingBlockFactory {
	Context context;
	
	@Override
	public BuildingBlockInstance createBuildingBlock(String buildingBlockName) {
		// TODO Auto-generated method stub
		BuildingBlockInstance inst = super.createBuildingBlock(buildingBlockName);
		if (inst instanceof AndroidBuildingBlockInstance)
			((AndroidBuildingBlockInstance)inst).setContext(context);
		return inst;
	}

	private static final Map<String,Class<? extends BuildingBlockInstance>> MAP = createMap();
	
	private static Map<String,Class<? extends BuildingBlockInstance>> createMap() {
		Map<String, Class<? extends BuildingBlockInstance>> classMap = new HashMap<String,Class<? extends BuildingBlockInstance>>();
		
		classMap.put("ArrivingAtAnyPoI", PoiTrigger.class);
		classMap.put("NotifyLocation", PoiNotification.class);
		return classMap;
	}
	
	public CompositionFactory() {
		super(MAP);
	}

	@Override
	public void setContext(Context context) {
		this.context = context;		
	}
	

}
