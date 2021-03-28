package json.restrictions.room;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;

public enum ChallengeRoomRestriction implements RestrictionPointer
{
	ACTION_NAME(Restriction.ACTION_NAME);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ChallengeRoomRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ChallengeRoomRestriction(Restriction restriction)
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
