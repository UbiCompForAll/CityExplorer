/**
 * @contributor(s): Christian Skjetne (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:
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

/**
 * @description:
 *
 * 
 */

package org.ubicompforall.CityExplorer.data;

public class Time
{

	/** The hour of the time object. */
	public int hour;
	
	/** The minute of the time object. */
	public int minute;
	
	/**
	 * Instantiates a new time object.
	 *
	 * @param hour The hour.
	 * @param minute The minute.
	 */
	public Time(int hour, int minute)
	{
		this.hour = hour;
		this.minute = minute;
	}
}