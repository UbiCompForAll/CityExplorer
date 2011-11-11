package org.ubicompforall.CityExplorer.data;

import android.widget.Adapter;

/**
 * Class that handles the sections of {@link SeparatedListAdapter}.
 * @author Christian Skjetne.
 *
 */
public class Section implements Comparable<Section>
{
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
	public Adapter getAdapter()
	{
		return adapter;
	}
	
	/**
	 * Sets the adapter.
	 * @param adapter The adapter you want to set.
	 */
	public void setAdapter(Adapter adapter)
	{
		this.adapter = adapter;
	}
	
	/**
	 * Gets the caption of the adapter.
	 * @return The name of the caption.
	 */
	public String getCaption()
	{
		return caption;
	}
	
	/**
	 * Sets the caption of the adapter.
	 * @param caption The name of the adapter's caption.
	 */
	public void setCaption(String caption)
	{
		this.caption = caption;
	}
	
	@Override
	public String toString()
	{
		return caption;
	}

	@Override
	public int compareTo(Section another)
	{
		return caption.compareTo(another.getCaption());
	}
}
