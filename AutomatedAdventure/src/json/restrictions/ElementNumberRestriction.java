package json.restrictions;

public enum ElementNumberRestriction implements RestrictionPointer
{
	MAX_VALUE(Restriction.MAX_VALUE), MIN_VALUE(Restriction.MIN_VALUE), MULTIPLIER(Restriction.MULTIPLIER, true);

	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ElementNumberRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ElementNumberRestriction(Restriction restriction)
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
