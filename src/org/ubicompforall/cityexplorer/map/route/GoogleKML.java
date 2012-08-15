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
 * This class keeps hold of the tabs used in planning mode.
 * 
 */


package org.ubicompforall.cityexplorer.map.route;
//OBSOLETE !!!
/*
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final public class GoogleKML {

	public static Road getRoad(	double fromLat, 
								double fromLon, double toLat, 
												double toLon) {

		StringBuffer	sb = new StringBuffer();
		KMLHandler 		ha = new KMLHandler();
		InputStream		is = null;

		sb.append("http://maps.google.com/maps?f=d&hl=en");
		sb.append("&saddr=");
		sb.append(Double.toString(fromLat));
		sb.append(",");
		sb.append(Double.toString(fromLon));
		sb.append("&daddr=");
		sb.append(Double.toString(toLat));
		sb.append(",");
		sb.append(Double.toString(toLon));
		sb.append("&dirflg=w"); 					// walking
		sb.append("&ie=UTF8&0&om=0&output=kml");

		try {
			SAXParser 		parser	= SAXParserFactory.newInstance().newSAXParser();
			URLConnection 	conn	= new URL(sb.toString()).openConnection();
							is		= conn.getInputStream();

			parser.parse(is , ha);
			is.close();
		} catch (ParserConfigurationException e) { e.printStackTrace();
		} catch (SAXException e) { e.printStackTrace();
		} catch (MalformedURLException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace();
		}

		return ha.road;
	}//getRoad


private final static class KMLHandler extends DefaultHandler {
	private Road road;
	private boolean isPlacemark;
	private boolean isRoute;
	private boolean isItemIcon;
	private String content;

	private KMLHandler() {
		road = new Road();
	}

	public void startElement(	String uri, 
								String tag, 
								String name, 
								Attributes attributes) throws SAXException {

		if (tag.equalsIgnoreCase("Placemark")) {
			isPlacemark = true;
			road.addPoint();
		} else if (tag.equalsIgnoreCase("ItemIcon")) {
			if (isPlacemark){
				isItemIcon = true;
			}
		}
		content = new String();
	}


	public void endElement(		String uri, 
								String tag, 
								String name) throws SAXException {

		tag = tag.toLowerCase();

		boolean elName 	= tag.equals("name");
		boolean color	= tag.equals("color");
		boolean width	= tag.equals("width");
		boolean descr	= tag.equals("description");
		boolean href	= tag.equals("href");
		boolean coords	= tag.equals("coordinates");
		boolean plcmrk	= tag.equals("placemark");
		boolean icon	= tag.equals("itemicon");

		int		last	= road.points.length-1;



		if (content.length() > 0) {



				if (elName) {

						if (isPlacemark) { 
							isRoute = content.equalsIgnoreCase("Route");
							if (!isRoute) { 
								road.points[last].name = content; }
						} else {
							road.name = content;
						}

		} else	if (color && !isPlacemark) {

						road.color = Integer.parseInt(content, 16);

		} else	if (width && !isPlacemark) {

						road.width = Integer.parseInt(content);

		} else	if (descr) {

						if (isPlacemark) {
							String description = content
												.replaceAll("<br/>","")
												.replaceAll("&#160;","");

							if (!isRoute){
								road.points[last].description = description;
							} else {
								road.description = description;
							}
						}

		} else	if (href) {

						if (isItemIcon) {
							road.points[last].iconUrl = content;
						}

		} else	if (coords) {

						if (isPlacemark) {
							if (!isRoute) {

								String[] 	ll	= content.split(",");
								double 		lon = Double.parseDouble(ll[0]);
								double 		lat = Double.parseDouble(ll[1]);

								road.points[last].latitude 	= lat;
								road.points[last].longitude	= lon;

							} else {

								String[] 	turns 	= content.split(" ");
								road.route 		= new double[turns.length][2];

								for (int i = 0; i < turns.length; i++) {
									String[] ll = turns[i].split(",");
								//for (int j = 0; j < 2 && j < ll.length; j++)
									for (int j = 0; j < 2 ; j++)
										road.route[i][j] = Double 
											.parseDouble(ll[j]);
								}
							}
						}
		}
		}

		if (plcmrk) { 
						isPlacemark = false;
						if (isRoute){
							isRoute = false;
						}
		} else if (icon) {
						if (isItemIcon){
							isItemIcon = false;
						}
		}
	}

	public void characters(char[] ch, int start, int length) 
							throws SAXException {
		String chars = new String(ch, start, length).trim();
		content = content.concat(chars);
	}
}

}

*/