package json.restrictions;

public enum RandomRedirectRestriction implements RestrictionPointer
{
	NUMBER_MAP(Restriction.NUMBER_MAP), ELEMENT_ADJUSTMENTS(Restriction.ELEMENT_ADJUSTMENTS, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private RandomRedirectRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private RandomRedirectRestriction(Restriction restriction)
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
