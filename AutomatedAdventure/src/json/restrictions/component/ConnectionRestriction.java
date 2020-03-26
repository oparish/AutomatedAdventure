package json.restrictions.component;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;

public enum ConnectionRestriction implements RestrictionPointer
{
	FIRST(Restriction.FIRST), SECOND(Restriction.SECOND), NAME(Restriction.NAME);
	
	private Restriction restriction;
	
	private ConnectionRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
