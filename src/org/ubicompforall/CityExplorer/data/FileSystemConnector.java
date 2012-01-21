/**
 * @contributor(s): Rune Sætre (NTNU)
 * @version: 		0.1
 * @date:			15 December 2011
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
 * This class tracks existing DBs.
 * This class keeps hold of the tabs used in import mode.
 */

package org.ubicompforall.CityExplorer.data;

import java.util.ArrayList;

public class FileSystemConnector implements FileSystemInterface {

	@Override
	public ArrayList<DB> getAllDBs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<DB> getAllDBs(String category) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DB getDB(int privateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteDB(DB db) {
		// TODO Auto-generated method stub
		return false;
	}//deleteDB

}//FileSystemConnector
