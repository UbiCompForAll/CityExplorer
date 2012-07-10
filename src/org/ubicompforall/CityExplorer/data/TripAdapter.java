/**
 * @contributor(s): Jacqueline Floch (SINTEF), Rune Sætre (NTNU)
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

import java.util.ArrayList;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ubicompforall.CityExplorer.R;

/**
 * This class handles the trip adapters.
 */
public class TripAdapter extends ArrayAdapter<Trip> {

	/** The items in the trip adapter. */
	private ArrayList<Trip> items;

	/** The context. */
	Context context;

	/** The text view resource id. */
	int textViewResourceId;

	/**
	 * Instantiates a new trip adapter.
	 *
	 * @param context The context.
	 * @param textViewResourceId The text view resource id.
	 * @param items The items.
	 */
	public TripAdapter(Context context, int textViewResourceId, ArrayList<Trip> items) {
		super(context, textViewResourceId, items);
		this.items = items;
		this.context = context;
		this.textViewResourceId = textViewResourceId;
	}

	@Override
	public void remove(Trip t)
	{
		DBFactory.getInstance(context).deleteTrip(t);
		super.remove(t);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View v = convertView;

		Trip t = items.get(pos);

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(textViewResourceId, null);
		}

		if (t != null) {
			TextView label = (TextView) v.findViewById(R.id.label);
			TextView descr = (TextView) v.findViewById(R.id.description);
			if (label != null) {
				label.setText(t.getLabel());
			}
			if (descr != null) {
				descr.setText(t.getDescription());
			}
		}
		return v;
	}
}