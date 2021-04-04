package json.restrictions;

public enum PositionAdjustmentRestriction implements RestrictionPointer
{
	MAP_NAME(Restriction.MAP_NAME), ELEMENT_NAME(Restriction.ELEMENT_NAME), ADJUSTMENT_TYPE(Restriction.ADJUSTMENT_TYPE);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private PositionAdjustmentRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private PositionAdjustmentRestriction(Restriction restriction)
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
