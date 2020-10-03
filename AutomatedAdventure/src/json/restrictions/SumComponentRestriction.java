package json.restrictions;

public enum SumComponentRestriction implements RestrictionPointer
{
	NUMBER_REFERENCE(Restriction.NUMBER_REFERENCE), NUMBER_VALUE(Restriction.NUMBER_VALUE), SUM_SIGN(Restriction.SUM_SIGN);
	
	private Restriction restriction;
	
	private SumComponentRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
