package json.restrictions;

public enum AdjustmentDataRestriction implements RestrictionPointer 
{
	ELEMENT_ADJUSTMENTS(Restriction.ELEMENT_ADJUSTMENTS, true), POSITION_ADJUSTMENTS(Restriction.POSITION_ADJUSTMENTS, true), 
		COUNTER_ADJUSTMENTS(Restriction.COUNTER_ADJUSTMENTS, true), COUNTER_INITIALISATIONS(Restriction.COUNTER_INITIALISATIONS, true), 
		MAKE_ELEMENTS(Restriction.MAKE_ELEMENTS, true), REMOVE_ELEMENTS(Restriction.REMOVE_ELEMENTS, true),	
		MAKE_CONNECTIONS(Restriction.MAKE_CONNECTIONS, true);
	
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
