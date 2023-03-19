package json.restrictions;

public enum RevealRestriction implements RestrictionPointer
{
	POSITION_TYPE(Restriction.POSITION_TYPE), POSITION_COUNTER_NAME(Restriction.POSITION_COUNTER_NAME, true), 
	BOOLEAN_VALUE(Restriction.BOOLEAN_VALUE), MAP_NAME(Restriction.MAP_NAME);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private RevealRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private RevealRestriction(Restriction restriction)
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
