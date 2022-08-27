package json.restrictions;

public enum CounterInitialisationRestriction implements RestrictionPointer
{
	COUNTER_NAME(Restriction.COUNTER_NAME, false), COUNTER_PRIMARY_TYPE(Restriction.COUNTER_PRIMARY_TYPE, false), 
	COUNTER_SECONDARY_TYPE(Restriction.COUNTER_SECONDARY_TYPE, false), 	MAP_NAME(Restriction.MAP_NAME, false);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private CounterInitialisationRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private CounterInitialisationRestriction(Restriction restriction)
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
