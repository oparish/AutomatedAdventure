package json.restrictions;

public enum ActionRestriction implements RestrictionPointer
{
	SHOWN_NAME(Restriction.SHOWN_NAME), KEY_NAME(Restriction.KEY_NAME);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ActionRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ActionRestriction(Restriction restriction)
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
