package json.restrictions.room;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;

public enum TimedRoomRestriction implements RestrictionPointer
{
	;
	
	private Restriction restriction;
	
	private TimedRoomRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
