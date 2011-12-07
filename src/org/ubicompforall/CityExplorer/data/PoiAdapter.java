/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			23 May 2011
 * @revised:
 *
 * Copyright (C) 2011 UbiCompForAll Consortium (SINTEF, NTNU)
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

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ubicompforall.CityExplorer.R;

/**
 * This class handles the poi adapters.
 */
public class PoiAdapter extends ArrayAdapter<Poi> {

	/** The items in the poi adapter. */
	private ArrayList<Poi> items;

	/** The context. */
	private Context context;

	/** The text view resource id. */
	int textViewResourceId;

	/**
	 * Instantiates a new poi adapter.
	 *
	 * @param context The context.
	 * @param textViewResourceId The text view resource id.
	 * @param items The items to add to the adapter.
	 */
	public PoiAdapter(Context context, int textViewResourceId, ArrayList<Poi> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
		this.textViewResourceId = textViewResourceId;
	}

	/**
	 * Replace all pois in the list of pois.
	 *
	 * @param pois The pois.
	 */
	public void replaceAll(ArrayList<Poi> pois)
	{
		items.clear();
		items.addAll(pois);
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Poi p = items.get(pos);

		if (v == null) {
			v = vi.inflate(textViewResourceId, null);
		}

		if (p != null) {
			TextView label = (TextView) v.findViewById(R.id.label);
			TextView descr = (TextView) v.findViewById(R.id.description);

			if (label != null) { label.setText(p.getLabel()); }
			if (descr != null) { descr.setText(p.getDescription()); }
		}
		return v;
	}
}