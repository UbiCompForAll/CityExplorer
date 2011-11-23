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

final public class PoiAddress {

	/**
	 * Field for containing the street name.
	 */
	private final String 	street;
	/**
	 * Field for containing the zip code.
	 */
	private final int		zipCode;
	/**
	 * Field for containing the name of the city.
	 */
	private final String	place;
	/**
	 * Field for containing position latitude.
	 */
	private final double 	latitude;
	/**
	 * Field for containing position longitude.
	 */
	private final double 	longitude;
	/**
	 * Field for containing a hash code.
	 */
	private volatile int hashCode;


	/**
	 * A Builder for the Address class. 
	 */
	public static class Builder {
		// Required parameters
		/** The name of a place. */
		private final String	place;

		// default values for optional fields
		/** The street of an address. */
		private String	street		= "";

		/** The zip code of an address. */
		private int		zipCode		= 0;

		/** The latitude of an address. */
		private double	latitude	= 0d;

		/** The longitude of an address. */
		private double	longitude	= 0d;

		/**
		 * Instantiates a new builder.
		 *
		 * @param place The name of a place.
		 */
		public Builder(String place){
			this.place = place;;
		}

		/**
		 * Add a street to an address.
		 *
		 * @param s The street name.
		 * @return The builder.
		 */
		public Builder street(String s){
			this.street	= s;
			return this;
		}

		/**
		 * Add a latitude to an address.
		 *
		 * @param la The latitude.
		 * @return The builder.
		 */
		public Builder latitude(double la){
			this.latitude = la;
			return this;
		}

		/**
		 * Add a longitude to an address.
		 *
		 * @param lo The longitude.
		 * @return The builder.
		 */
		public Builder longitude(double lo){
			this.longitude = lo;
			return this;
		}

		/**
		 * Add a Zip code to an address.
		 *
		 * @param zip The zip code.
		 * @return The builder.
		 */
		public Builder zipCode(int  zip){
			this.zipCode = zip;
			return this;
		}

		/**
		 * Builds the address. Call this method after adding elements to the builder. 
		 *
		 * @return The address of a poi.
		 */
		public PoiAddress build() {
			return new PoiAddress(this); 
		}
	}

	/**
	 * Constructor that builds and address object.
	 *
	 * @param b The builder.
	 */
	public PoiAddress(Builder b){
		this.street		= b.street;
		this.place		= b.place;
		this.zipCode	= b.zipCode;
		this.latitude	= b.latitude;
		this.longitude	= b.longitude;
	}

	/**
	 * Method for being able to modify a PoiAddress object.
	 * @return A Builder object for the address.
	 */
	public Builder modify() {

		Builder temp = new Builder(this.getCity());
		temp.street(this.getStreet());
		temp.zipCode(this.getZipCode());
		temp.latitude(this.getLatitude());
		temp.longitude(this.getLongitude());
		return temp;
	}

	/**
	 * Method for getting the street of the address.
	 * @return The street name.
	 */
	public String getStreet(){
		return street;
	}

	/**
	 * Method for getting the city of the address.
	 * @return The name of the city.
	 */
	public String getCity(){
		return place;
	}

	/**
	 * Method for getting the zip code of the address.
	 * @return The zip code.
	 */
	public int getZipCode(){
		return zipCode;
	}

	/**
	 * Method for getting the longitude of the address.
	 * @return The longitude of the address.
	 */
	public double getLongitude(){
		return longitude;
	}

	/**
	 * Method for getting the latitude of the address.
	 * @return The latitude of the address.
	 */
	public double getLatitude(){
		return latitude;
	}

	@Override
	public boolean equals(Object obj){
		PoiAddress cmprnd = null;

		if (obj == null) {
			return false;
		}

		if (obj instanceof PoiAddress){
			cmprnd = (PoiAddress) obj;
		} else {
			return false;
		}

		boolean same = 
			place.		equals(cmprnd.getCity())	&&
			street.		equals(cmprnd.getStreet())	&&
			zipCode		== cmprnd.getZipCode()	&&
			latitude	== cmprnd.getLatitude()	&& 
			longitude	== cmprnd.getLongitude();
		return same;
	}

	@Override
	public int hashCode() {
		int result = hashCode;

		long llat = Double.doubleToLongBits(latitude);
		long llon = Double.doubleToLongBits(longitude);

		if (result == 0){
			result = 17;
			result = 31 * result + place.hashCode();
			result = 31 * result + street.hashCode();
			result = 31 * result + zipCode;
			result = 31 * result + (int) (llat^(llat>>>32));
			result = 31 * result + (int) (llon^(llon>>>32));
			hashCode = result;
		}
		return result;
	}

	@Override
	public String toString(){

		StringBuilder sb = new StringBuilder();
		sb.append("place("		+place+		")");
		sb.append("zipCode("	+zipCode+	")");
		sb.append("street("		+street+	")");
		sb.append("longitude("	+longitude+	")");
		sb.append("latitude("	+latitude+	")");
		return sb.toString();
	}
}