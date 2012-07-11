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

import java.util.Map;

import org.ubicompforall.simplelanguage.runtime.AbstractStepInstance;
import org.ubicompforall.simplelanguage.runtime.TaskInstance;
import org.ubicompforall.simplelanguage.runtime.android.AndroidBuildingBlockInstance;

import android.content.Context;

public class PoiNotification extends AbstractStepInstance implements AndroidBuildingBlockInstance {
	Context context;

	@Override
	public int execute(TaskInstance context, Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setContext(Context context) {
		this.context = context;

		
	}

}
