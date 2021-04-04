package json.restrictions;

public enum RedirectRestriction implements RestrictionPointer
{
	CONTEXT_CONDITIONS(Restriction.CONTEXT_CONDITIONS, true), FIRST(Restriction.FIRST), SECOND(Restriction.SECOND), 
	ADJUSTMENT_DATA(Restriction.ADJUSTMENT_DATA, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private RedirectRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private RedirectRestriction(Restriction restriction)
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
