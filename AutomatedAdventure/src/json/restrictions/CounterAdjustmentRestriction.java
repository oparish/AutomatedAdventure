package json.restrictions;

public enum CounterAdjustmentRestriction implements RestrictionPointer 
{
	COUNTER_NAME(Restriction.COUNTER_NAME, false), COUNTER_ADJUSTMENT_TYPE(Restriction.COUNTER_ADJUSTMENT_TYPE, false);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private CounterAdjustmentRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private CounterAdjustmentRestriction(Restriction restriction)
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
