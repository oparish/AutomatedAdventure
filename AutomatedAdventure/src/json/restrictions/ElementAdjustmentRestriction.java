package json.restrictions;

public enum ElementAdjustmentRestriction implements RestrictionPointer
{
	CONNECTION_NAME(Restriction.CONNECTION_NAME, true), ELEMENT_NAME(Restriction.ELEMENT_NAME), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY), 
	NUMBER_VALUE(Restriction.NUMBER_VALUE, true), SUM_NAME(Restriction.SUM_NAME, true), TYPE(Restriction.TYPE), SUM_SIGN(Restriction.SUM_SIGN);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ElementAdjustmentRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ElementAdjustmentRestriction(Restriction restriction)
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
