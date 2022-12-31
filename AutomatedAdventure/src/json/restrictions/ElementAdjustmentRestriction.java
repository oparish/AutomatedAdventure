package json.restrictions;

public enum ElementAdjustmentRestriction implements RestrictionPointer
{
	ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY), TARGET_IDENTIFICATION(Restriction.TARGET_IDENTIFICATION),
	NUMBER_VALUE(Restriction.NUMBER_VALUE, true), SUM_NAME(Restriction.SUM_NAME, true), SUM_SIGN(Restriction.SUM_SIGN), ELEMENT_CONDITIONS(Restriction.ELEMENT_CONDITIONS, true);
	
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
