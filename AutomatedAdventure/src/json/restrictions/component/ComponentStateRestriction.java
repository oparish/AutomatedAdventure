package json.restrictions.component;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;

public enum ComponentStateRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), DESCRIPTION(Restriction.DESCRIPTION);
	
	private Restriction restriction;
	
	private ComponentStateRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
