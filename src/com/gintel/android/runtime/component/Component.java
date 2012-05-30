package com.gintel.android.runtime.component;

import java.util.Map;

import android.content.Context;

/**
 * Every Component have a name, cid, compositionType and a type.
 * Sub-classes of Component have more attributes based on which type of component it is.
 * 
 * Execute() performs the step
 * 
 * @author frank, john_edvard
 *
 */
public abstract class Component {
	private String name;
	private int id;
	private int compositionType;
	private int type;
	private int compositionId;
	
	//public static final int INCOMING_CALL_COMPONENT= -1;
	public static final int FILTER_COMPONENT = 1;
	public static final int CALL_CONTROL_COMPONENT = 2;
	public static final int AGENDA_COMPONENT = 3;
	public static final int NOTIFICATION_COMPONENT = 4;
	public static final int SMS_FILTER_COMPONENT = 5;
	public static final int SEND_SMS_COMPONENT = 6;
	public static final int CONTACTS_COMPONENT = 7;
	public static final int PROMPT_AND_COLLECT_COMPONENT = 8;
	public static final int GPS_COMPONENT = 9;

	//RS-120525
	public static final int URLGET_COMPONENT = 10; //extend Component
	
	public Component(int compositionType, int compositionId, int type, int id, String name){	
		this.compositionType = compositionType;
		this.compositionId = compositionId;
		this.type = type;
		this.id = id;
		this.name = name;
	}
	
	/**
	 * This method is called every time a matching event is found in the list of events in Engine.
	 * Every component override this method and this method will keep on going until it returns -1.
	 * The last component in the composition should keep its nextCid = -1. Otherwise, you'll get a RuntimeException.
	 * 
	 * @param context the context this component is running in
	 * 
	 * @param parameters save useful information and that information can be used by other components 
	 * which is in the same composition.
	 * Typical: Position=X, Time=Y, CallerNr=Z, Key=Val
	 * Passed from one component to the next.
	 * 
	 * @return the nextCid, so the correct component will get executed by Engine.
	 */
	public abstract int execute (Context context, Map<String, Object> parameters);
	
	/**
	 * Not in use
	 */
	public abstract void load();
	/**
	 * Not in use
	 */
	public abstract void save();
	
	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	public int getCompositionType(){
		return compositionType;
	}
	
	@Override
	public String toString() {
		return getId() + ": " + getName() + " (" + getClass().getSimpleName() + ")";
	}


	public int getType() {
		return type;
	}

	public void setCompositionId(int compositionId) {
		this.compositionId = compositionId;
	}

	public int getCompositionId() {
		return compositionId;
	}
}
