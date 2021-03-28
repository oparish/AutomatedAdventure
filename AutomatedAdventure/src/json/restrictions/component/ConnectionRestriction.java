package json.restrictions.component;

import json.restrictions.Restriction;
import json.restrictions.RestrictionPointer;

public enum ConnectionRestriction implements RestrictionPointer
{
	FIRST(Restriction.FIRST), SECOND(Restriction.SECOND);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ConnectionRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ConnectionRestriction(Restriction restriction)
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
