package json.restrictions;

public enum AdjustmentDataRestriction implements RestrictionPointer 
{
	ELEMENT_ADJUSTMENTS(Restriction.ELEMENT_ADJUSTMENTS, true), POSITION_ADJUSTMENTS(Restriction.POSITION_ADJUSTMENTS, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private AdjustmentDataRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private AdjustmentDataRestriction(Restriction restriction)
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
