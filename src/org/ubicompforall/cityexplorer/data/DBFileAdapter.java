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
 * Adapter used by the ImportLocalTab-list to show sections and (corresponding) DBFile-names 
 */

package org.ubicompforall.cityexplorer.data;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.ubicompforall.cityexplorer.CityExplorer;
import org.ubicompforall.cityexplorer.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * This class handles the DB adapters.
 */
public class DBFileAdapter extends ArrayAdapter<DB> {

	/** The items in the DB adapter. */
	private CopyOnWriteArrayList<DB> items;

	/** The context. */
	private Context context;

	/** The text view resource id. */
	int textViewResourceId;

	
	/**
	 * Instantiates a new DB adapter.
	 * @param context The context.
	 * @param textViewResourceId The text view resource id.
	 * @param webDBs The items to add to the adapter.
	 */
	public DBFileAdapter(Context context, int textViewResourceId, CopyOnWriteArrayList<DB> webDBs) {
		super(context, textViewResourceId, webDBs);
		this.items = webDBs;
		this.context = context;
		this.textViewResourceId = textViewResourceId;
	}

	
	private void debug( int level, String message ) {
		CityExplorer.debug( level, message );		
	} //debug


	/**
	 * Replace all DBs in the list of DBs.
	 * @param dbs The DBs.
	 */
	public void replaceAll( ArrayList<DB> dbs ){
		items.clear();
		if (items == null){
			debug(0, "DBFileAdapter~81: OOooppss!!... items==null");
		}else if( dbs == null ){
			debug(0, "DBFileAdapter~81: OOooppss!!... NO DBs (== null)");
		}else{
			items.addAll(dbs);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		DB p = items.get(pos);

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
	}//getView
}//DBFileAdapter
