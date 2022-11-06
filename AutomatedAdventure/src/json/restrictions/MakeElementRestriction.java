package json.restrictions;

public enum MakeElementRestriction implements RestrictionPointer 
{
	ELEMENT_NAME(Restriction.ELEMENT_NAME), NUMBER_VALUE(Restriction.NUMBER_VALUE, true), UNIQUE_NAME(Restriction.UNIQUE_NAME, true),
	INSTANCE_DETAILS(Restriction.INSTANCE_DETAILS, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private MakeElementRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private MakeElementRestriction(Restriction restriction)
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
