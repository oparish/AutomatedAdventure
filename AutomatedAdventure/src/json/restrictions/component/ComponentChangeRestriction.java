package json.restrictions.component;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;

public enum ComponentChangeRestriction implements RestrictionPointer
{
	OLD_COMPONENT_STATE_NAME(Restriction.OLD_COMPONENT_STATE_NAME), 
	NEW_COMPONENT_STATE_NAME(Restriction.NEW_COMPONENT_STATE_NAME), TRANSITION_TEXT(Restriction.TRANSITION_TEXT), TIME(Restriction.TIME),
	TRIGGER(Restriction.TRIGGER);
	
	private Restriction restriction;
	
	private ComponentChangeRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
	
	
}
