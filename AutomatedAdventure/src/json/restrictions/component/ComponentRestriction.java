package json.restrictions.component;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;

public enum ComponentRestriction implements RestrictionPointer 
{
	COMPONENT_STATES(Restriction.COMPONENT_STATES), COMPONENT_CHANGES(Restriction.COMPONENT_CHANGES), NAME(Restriction.NAME),
	INITIAL_COMPONENT_STATE_NAME(Restriction.INITIAL_COMPONENT_STATE_NAME), SHOWN_NAME(Restriction.SHOWN_NAME);
	
	private Restriction restriction;
	
	private ComponentRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
