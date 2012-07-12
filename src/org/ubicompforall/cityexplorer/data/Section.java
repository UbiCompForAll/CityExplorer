/**
 * @contributor(s): Christian Skjetne (NTNU), Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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

package org.ubicompforall.cityexplorer.data;

import android.widget.Adapter;

public class Section implements Comparable<Section>{
	/**
	 * Field containing the caption of a section.
	 */
	private String caption;
	
	/**
	 * Field containing the adapter.
	 */
	private Adapter adapter;

	/**
	 * Public constructor for creating an instance of Section.
	 * @param caption The caption of the section.
	 * @param adapter The adapter of the section.
	 */
	public Section(String caption, Adapter adapter) {
		this.caption=caption;
		this.adapter=adapter;
	}
	
	/**
	 * Gets the current adapter.
	 * @return The adapter.
	 */
	public Adapter getAdapter(){
		return adapter;
	}
	
	/**
	 * Sets the adapter.
	 * @param adapter The adapter you want to set.
	 */
	public void setAdapter(Adapter adapter){
		this.adapter = adapter;
	}
	
	/**
	 * Gets the caption of the adapter.
	 * @return The name of the caption.
	 */
	public String getCaption(){
		return caption;
	}
	
	/**
	 * Sets the caption of the adapter.
	 * @param caption The name of the adapter's caption.
	 */
	public void setCaption(String caption){
		this.caption = caption;
	}
	
	@Override
	public String toString(){
		return caption;
	}

	@Override
	public int compareTo(Section another){
		return caption.compareTo(another.getCaption());
	}
}//Section
