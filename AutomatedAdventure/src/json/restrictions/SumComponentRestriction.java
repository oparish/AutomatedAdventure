package json.restrictions;

public enum SumComponentRestriction implements RestrictionPointer
{
	NUMBER_REFERENCE(Restriction.NUMBER_REFERENCE, true), NUMBER_VALUE(Restriction.NUMBER_VALUE, true), SUM_SIGN(Restriction.SUM_SIGN);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private SumComponentRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private SumComponentRestriction(Restriction restriction)
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
