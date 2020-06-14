package json.restrictions;

public enum EachElementAdjustmentRestriction implements RestrictionPointer
{
	ELEMENT_NAME(Restriction.ELEMENT_NAME), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY), NUMBER_VALUE(Restriction.NUMBER_VALUE);
	
	private Restriction restriction;
	
	private EachElementAdjustmentRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
