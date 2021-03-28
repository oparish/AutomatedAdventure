package json.restrictions;

public enum ChanceRestriction implements RestrictionPointer
{
	PERCENTAGE(Restriction.PERCENTAGE), PRIORITY(Restriction.PRIORITY);
		
	private boolean optional;
	private Restriction restriction;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ChanceRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ChanceRestriction(Restriction restriction)
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
