/**
 * @contributor(s): Jaroslav Rakhmatoullin (NTNU), Jacqueline Floch (SINTEF), Rune S¾tre (NTNU)
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

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.maps.GeoPoint;

final public class Poi extends IntentPassable {

	/**
	 * Field containing a PoI's global ID.
	 */
	private final int		idGlobal;

	/**
	 * Field containing a PoI's local ID.
	 */
	private final int		idPrivate;

	/**
	 * Field containing the name of a PoI.
	 */
	private final String 	label;

	/**
	 * Field containing a detailed description of a PoI.
	 */
	private final String	description;

	/**
	 * Field containing the address of a PoI.
	 */
	private final PoiAddress	address;

	/**
	 * Field containing the category classification for a PoI.
	 */
	private final String	category;

	/**
	 * Field containing the favourite status of a PoI.
	 */
	private final boolean	favourite;

	/**
	 * Field containing information about the opening hours of a PoI.
	 */
	private final String 	openingHours;

	/**
	 * Field containing the telephone number for a PoI.
	 */
	private final String	telephone;

	/**
	 * Field containing the web page URL connected to a PoI.
	 */
	private final String	webPage;

	/**
	 * Field containing the URL of an image connected to a PoI.
	 */
	private final String	imageURL;

	/**
	 * Field for containing a geopoint location for identifying locations on a google map.
	 */
	private volatile GeoPoint	geoPoint;

	/** The hash code. */
	private volatile int		hashCode;

	/**
	 * A Builder for the Poi class. 
	 */
	public static class Builder {

		/** The name of a poi. */
		private String	label;

		/** The address of a poi. */
		private PoiAddress	address;

		/** The global id of a poi. */
		private int		idGlobal	= -1;

		/** The private id of a poi. */
		private int		idPrivate	= -1;

		/** The description of a poi. */
		private String	description	= "";

		/** A boolean describing if this poi is a favorite or not. */
		private boolean	favourite	= false;

		/** The category the poi is assigned. */
		private String  category 	= "";

		/** The opening hours of a poi. */
		private	String 	openingHours	="";

		/** The telephone number of a poi. */
		private	String	telephone		="";

		/** The web page of a poi. */
		private	String	webPage			="";

		/** A String describing the image url of a poi. */
		private String 	imageURL		="";

		/**
		 * The required fields of the class are given as parameters to the constructor of the Builder.
		 * @param label	The name of a poi.
		 * @param address The address of a poi.
		 */
		public Builder(String label, PoiAddress address){
			this.label = label;
			this.address = address;
		}

		/**
		 * Method for populating the description field.
		 *
		 * @param d A description of a poi.
		 * @return The instance of the calling Builder class (returns itself).
		 */
		public Builder description(String d){
			this.description = d;
			return this;
		}

		/**
		 * Method for populating the global ID field.
		 * @param gid A PoI's global ID number.
		 * @return The instance of the calling Builder class (returns itself).
		 */
		public Builder idGlobal(int gid){
			this.idGlobal = gid;
			return this;
		}

		/**
		 * Method for populating the private ID field.
		 * @param pid A PoI's local ID number.
		 * @return the instance of the calling Builder class (returns itself).
		 */
		public Builder idPrivate(int pid)		{this.idPrivate		= pid;	return this;}

		/**
		 * Method for populating the category field.
		 * @param cat The category of a poi.
		 * @return The instance of the calling Builder class (returns itself).
		 */
		public Builder category(String cat){
			this.category = cat;
			return this;
		}

		/**
		 * Method for populating the favorite field.
		 * @param fav A PoI's favorite status
		 * @return The instance of the calling Builder class (returns itself)
		 */
		public Builder favourite(boolean fav){
			this.favourite	= fav;
			return this;
		}

		/**
		 * Method for populating the opening hours field.
		 * @param oh A PoI's opening hours.
		 * @return The instance of the calling Builder class (returns itself).
		 */
		public Builder openingHours(String oh){
			if(oh==null){
				return this;
			}else{	 
				this.openingHours = oh;
				return this;
			}
		}

		/**
		 * Method for populating the web page field.
		 * @param url The web page connected to a PoI.	
		 * @return The instance of the calling Builder class (returns itself).
		 */
		public Builder webPage(String url){
			if(url==null) {
				return this;	
			}else {
				this.webPage = url;
				return this;
			}
		}

		/**
		 * Method for populating the telephone field.
		 * @param tlf the telephone number of a PoI
		 * @return the instance of the calling Builder class (returns itself)
		 */
		public Builder telephone(String tlf){
			if(tlf==null){
				return this;	 
			}else {
				this.telephone = tlf;
				return this;
			}	
		}			

		/**
		 * Method for populating the label field.
		 *
		 * @param l The name of a PoI.
		 * @return The builder
		 */
		public Builder label(String l){
			this.label = l;
			return this;
		}
		
		/**
		 * Method for populating the address field.
		 * @param a A PoI's address.
		 * @return the instance of the calling Builder class (returns itself).
		 */
		public Builder address(PoiAddress a){
			this.address = a;
			return this;
		}

		/**
		 * Method for populating the web adress field of an image connected to a PoI.
		 * @param url The url of an image.
		 * @return The instance of the calling Builder class (returns itself).
		 */
		public Builder imageURL(String url){
			if(url==null){				
				return this;	
			}else {
				this.imageURL = url;
				return this;
			}
		}
		
		/**
		 * Calls the Poi(Builder b) constructor that returns an immutable Poi.
		 * @return Poi A new Poi Object to be used anywhere in the system
		 */
		public Poi build() {
			return new Poi(this); 
		}
	}

	/**
	 * Constructor taking in the Builder object, creating a Poi object from it.
	 * @param b The real constructor(Builder).
	 */
	public Poi(Builder b){
		this.idGlobal		= b.idGlobal;
		this.idPrivate		= b.idPrivate;
		this.address		= b.address;
		this.description	= b.description;
		this.label			= b.label;
		this.favourite		= b.favourite;
		this.category		= b.category;
		this.openingHours	= b.openingHours;
		this.webPage		= b.webPage;
		this.telephone		= b.telephone;
		this.imageURL		= b.imageURL;
	}

	/**
	 * Method making it possible to modify a PoI.
	 * @return The Builder object for modifying the PoI.
	 */
	public Builder modify() {
		Builder temp = new Builder(this.getLabel(), this.getAddress());
		temp.description(this.getDescription());
		temp.idGlobal(this.getIdGlobal());
		temp.idPrivate(this.getIdPrivate());
		temp.favourite(this.isFavourite());
		temp.category(this.getCategory());
		temp.openingHours(this.getOpeningHours());
		temp.webPage(this.getWebPage());
		temp.telephone(this.getTelephone());
		temp.imageURL(this.getImageURL());
		return temp;
	}

	/**
	 * Gets the name of a poi.
	 *
	 * @return The name of a poi.
	 */
	public String getLabel(){
		return label;
	}

	/**
	 * Gets the description of a poi.
	 *
	 * @return The description of a poi.
	 */
	public String getDescription(){
		return description;
	}

	/**
	 * Gets the address of a poi.
	 *
	 * @return The address of a poi.
	 */
	public PoiAddress getAddress(){
		return address;
	}

	/**
	 * Gets the global id of a poi.
	 *
	 * @return The global id of a poi.
	 */
	public int getIdGlobal(){
		return idGlobal;
	}

	/**
	 * Gets the private id of a poi.
	 *
	 * @return The private id of a poi.
	 */
	public int getIdPrivate(){
		return idPrivate;
	}

	/**
	 * Checks if the poi is a favourite.
	 *
	 * @return True if the poi is a favourite, false otherwise.
	 */
	public boolean isFavourite(){
		return	favourite;
	}

	/**
	 * Gets the category of a poi.
	 *
	 * @return The category of a poi.
	 */
	public String getCategory(){
		return category;
	}

	/**
	 * Gets the telephone number of a poi.
	 *
	 * @return The telephone number of a poi.
	 */
	public String getTelephone(){
		return telephone;
	}

	/**
	 * Gets the opening hours of a poi.
	 *
	 * @return The opening hours of a poi.
	 */
	public String getOpeningHours(){
		return openingHours;
	}

	/**
	 * Gets the web page of a poi.
	 *
	 * @return The web page of a poi.
	 */
	public String getWebPage(){
		return webPage;
	}

	/**
	 * Gets the url of the image.
	 *
	 * @return The url of the image.
	 */
	public String getImageURL(){
		return imageURL;
	}

	/**
	 * Gets the geo point of a poi's address.
	 *
	 * @return The geo point of a poi's address.
	 */
	public GeoPoint				getGeoPoint(){
		GeoPoint gp = geoPoint;
		if (gp == null){
			int latE6 	= (int)(address.getLatitude() * 1e6);
			int lonE6 	= (int)(address.getLongitude() * 1e6);
			gp 			= new GeoPoint(latE6, lonE6);
			geoPoint 	= gp;
		}
		return gp;
	}	

	@Override public boolean equals(Object obj){
		Poi cmprnd = null;

		if (obj == null){
			return false;
		}

		if (obj instanceof Poi){
			cmprnd = (Poi) obj;
		} else {
			return false;
		}

		boolean same = 
			idGlobal	== cmprnd.getIdGlobal()			&&
			idPrivate	== cmprnd.getIdPrivate()		&&
			favourite	== cmprnd.isFavourite()			&&
			telephone.	equals(cmprnd.getTelephone())	&&
			label.		equals(cmprnd.getLabel())		&&
			description.equals(cmprnd.getDescription())	&&
			address.	equals(cmprnd.getAddress())		&&
			category.	equals(cmprnd.getCategory())	&&
			//categories.equals(cmprnd.getCategories())
			openingHours.equals(cmprnd.getOpeningHours()) &&
			imageURL.	equals(cmprnd.getImageURL())	&&
			webPage.	equals(cmprnd.getWebPage());

		return same;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("global("		+idGlobal+				")");
		sb.append("private("	+idPrivate+				")");
		sb.append("label("		+label+					")");
		sb.append("desription("	+description+			")");
		sb.append("address("	+address.toString()+	")");
		sb.append("favourite("	+( favourite? "yes" : "no")+	")");
		sb.append("categories("	+category+				")");
		sb.append("openingHours("+openingHours+")");
		sb.append("webPage("	+webPage+")");
		sb.append("imageurl("	+imageURL+")");
		sb.append("telephone("	+telephone+")");

		return sb.toString();
	}

	@Override public int hashCode() {
		int result = hashCode;
		if (result == 0){
			result = 17;

			result = 31 * result + idGlobal;
			result = 31 * result + idPrivate;
			result = 31 * result + label.hashCode();
			result = 31 * result + description.hashCode();
			result = 31 * result + address.hashCode();
			result = 31 * result + (favourite? 1 : 0);
			result = 31 * result + category.hashCode();
			result = 31 * result + openingHours.hashCode();
			result = 31 * result + webPage.hashCode();
			result = 31 * result + telephone.hashCode();
			hashCode = result;
		}
		return result;
	}


	/**
	 * Instantiates a new poi. Parcelable interface.
	 *
	 * @param in The parcelable in.
	 */
	public Poi(Parcel in) {

		idGlobal	= in.readInt();
		idPrivate 	= in.readInt();

		label		= in.readString();
		description	= in.readString();
		address		= new PoiAddress.Builder(in.readString())
		.street(in.readString())
		.zipCode(in.readInt())
		.latitude(in.readDouble())
		.longitude(in.readDouble())
		.build();
		favourite	= (1==in.readInt());
		category	= in.readString();
		openingHours= in.readString();
		webPage		= in.readString();
		telephone	= in.readString();
		imageURL	= in.readString();
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {

		out.writeInt(idGlobal);
		out.writeInt(idPrivate);

		out.writeString(label);
		out.writeString(description);
		out.writeString(address.getCity()); 
		out.writeString(address.getStreet());
		out.writeInt(	address.getZipCode());
		out.writeDouble(address.getLatitude());
		out.writeDouble(address.getLongitude());
		out.writeInt(favourite? 1 : 0);
		out.writeString(category);
		out.writeString(openingHours);
		out.writeString(webPage);
		out.writeString(telephone);
		out.writeString(imageURL);
	}

	/**
	 * Parcelable.Creator Interface. The Constant CREATOR.
	 */
	public static final 
	Parcelable.Creator<Poi> CREATOR = new 
	Parcelable.Creator<Poi>() {
		public Poi createFromParcel(Parcel in) {
			return new Poi(in);
		}

		public Poi[] newArray(int size) {
			return new Poi[size];
		}
	};
}
