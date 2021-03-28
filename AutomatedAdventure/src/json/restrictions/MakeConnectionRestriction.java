package json.restrictions;

public enum MakeConnectionRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), NUMBER_VALUE(Restriction.NUMBER_VALUE);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private MakeConnectionRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private MakeConnectionRestriction(Restriction restriction)
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
