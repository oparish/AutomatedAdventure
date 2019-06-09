package json.restrictions;

import static json.restrictions.RestrictionType.CHALLENGEROOMRESTRICTION;
import static json.restrictions.RestrictionType.TIMEDROOMRESTRICTION;

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
		return new RestrictionType[] {CHALLENGEROOMRESTRICTION, TIMEDROOMRESTRICTION};
	}

}
