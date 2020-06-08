package json.restrictions;

public enum MakeConnectionRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), NUMBER_VALUE(Restriction.NUMBER_VALUE);
	
	private Restriction restriction;
	
	private MakeConnectionRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}
	
	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
