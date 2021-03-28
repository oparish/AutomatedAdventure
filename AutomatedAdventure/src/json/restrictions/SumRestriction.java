package json.restrictions;

public enum SumRestriction implements RestrictionPointer
{
	NUMBER_REFERENCE(Restriction.NUMBER_REFERENCE, true), NUMBER_VALUE(Restriction.NUMBER_VALUE, true), 
	SUM_COMPONENTS(Restriction.SUM_COMPONENTS);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private SumRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private SumRestriction(Restriction restriction)
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
