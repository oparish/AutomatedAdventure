package json.restrictions;

public enum SumRestriction implements RestrictionPointer
{
	NUMBER_REFERENCE(Restriction.NUMBER_REFERENCE), NUMBER_VALUE(Restriction.NUMBER_VALUE), SUM_COMPONENTS(Restriction.SUM_COMPONENTS);
	
	private Restriction restriction;
	
	private SumRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
