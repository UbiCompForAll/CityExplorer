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

package org.ubicompforall.CityExplorer.buildingblock;

import org.ubicompforall.simplelanguage.Task;
import org.ubicompforall.simplelanguage.runtime.TaskTrigger;
import org.ubicompforall.simplelanguage.runtime.TriggerMonitor;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.content.Context;

public class PoiTrigger implements TriggerMonitor, AndroidBuildingBlockInstance{
	Context context;

	@Override
	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void startMonitoring(Task task, TaskTrigger tasktrigger) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopMonitoring() {
		// TODO Auto-generated method stub
		
	}

}
