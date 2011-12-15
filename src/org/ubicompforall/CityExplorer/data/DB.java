package org.ubicompforall.CityExplorer.data;

public class DB {
	private String url = new String();

	public DB(String string) {
		url = string;
	}//CONSTRUCTOR

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getCategory() {
		// TODO Auto-generated method stub
		return "Runes Kategori";
	}

	public CharSequence getLabel() {
		// TODO Auto-generated method stub
		return url;
	}

	public CharSequence getDescription() {
		// TODO Auto-generated method stub
		return "Runes Beskrivæls";
	}

}//DB
