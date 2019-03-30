package json.restrictions;

public enum IntervalRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), TIME(Restriction.TIME);
	
	private Restriction restriction;
	
	private IntervalRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
