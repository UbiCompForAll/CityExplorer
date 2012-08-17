/**
 * 
 */
package org.ubicompforall.cityexplorer.map.route;

/**
 * @author satre
 *
 */
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.io.InputStream;

import org.ubicompforall.cityexplorer.CityExplorer;

import android.util.Log;

public class XMLParser {
	// names of the XML tags
	protected static final String MARKERS = "markers";
	protected static final String MARKER = "marker";

	protected URL feedUrl;

	protected XMLParser(final String feedUrl) {
		try {
			this.feedUrl = new URL(feedUrl);
		} catch (MalformedURLException e) {
			CityExplorer.debug(-1, e.getMessage()+", XML parser - " + feedUrl);
		}
	}

	protected InputStream getInputStream() {
		try {
			if (feedUrl == null){
				CityExplorer.debug(-1, "feedUrl = "+feedUrl );
			}else{
				return feedUrl.openConnection().getInputStream();
			}
		} catch (IOException e) {
			Log.e(e.getMessage(), "XML parser - " + feedUrl);
		}
		return null;
	}
}// class XMLParser
