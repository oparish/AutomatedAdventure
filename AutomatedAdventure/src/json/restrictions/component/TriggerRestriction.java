package json.restrictions.component;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;

public enum TriggerRestriction implements RestrictionPointer
{
	COMPONENT_NAME(Restriction.COMPONENT_NAME), COMPONENT_STATE_NAME(Restriction.COMPONENT_STATE_NAME);
	
	private Restriction restriction;
	
	private TriggerRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
