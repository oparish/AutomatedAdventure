package json.restrictions;

public enum GroupChoiceRestriction implements RestrictionPointer
{
	POSITION_COUNTER_NAME(Restriction.POSITION_COUNTER_NAME, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private GroupChoiceRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private GroupChoiceRestriction(Restriction restriction)
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
