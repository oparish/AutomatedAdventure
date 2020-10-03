package json.restrictions;

public enum ElementAdjustmentRestriction implements RestrictionPointer
{
	CONNECTION_NAME(Restriction.CONNECTION_NAME), ELEMENT_NAME(Restriction.ELEMENT_NAME), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY), 
	NUMBER_VALUE(Restriction.NUMBER_VALUE), SUM_NAME(Restriction.SUM_NAME), TYPE(Restriction.TYPE);
	
	private Restriction restriction;
	
	private ElementAdjustmentRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
