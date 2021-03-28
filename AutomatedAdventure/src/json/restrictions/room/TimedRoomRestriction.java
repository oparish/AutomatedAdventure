package json.restrictions.room;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;

public enum TimedRoomRestriction implements RestrictionPointer
{
	;
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private TimedRoomRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private TimedRoomRestriction(Restriction restriction)
	{
		this.optional = false;
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
