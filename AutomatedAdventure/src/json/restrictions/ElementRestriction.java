package json.restrictions;

public enum ElementRestriction implements RestrictionPointer
{
	ELEMENT_DATA(Restriction.ELEMENT_DATA), ELEMENT_NUMBERS(Restriction.ELEMENT_NUMBERS, true), ELEMENT_SETS(Restriction.ELEMENT_SETS, true), 
	UNIQUE(Restriction.UNIQUE);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ElementRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ElementRestriction(Restriction restriction)
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
