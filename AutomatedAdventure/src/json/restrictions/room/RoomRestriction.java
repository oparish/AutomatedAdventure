package json.restrictions.room;

import static json.restrictions.RestrictionType.CHALLENGEROOM;
import static json.restrictions.RestrictionType.TIMEDROOM;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;
import json.restrictions.RestrictionType;
import json.restrictions.SuperRestriction;

public enum RoomRestriction implements RestrictionPointer, SuperRestriction
{
	TEMPLATES(Restriction.TEMPLATES), NAME(Restriction.NAME);
	
	private Restriction restriction;
	
	private RoomRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
	
	@Override
	public RestrictionType[] getSubRestrictions()
	{
		return new RestrictionType[] {CHALLENGEROOM, TIMEDROOM};
	}

}
