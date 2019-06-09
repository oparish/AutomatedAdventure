package json.restrictions;

public enum ComponentStateRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), DESCRIPTION(Restriction.DESCRIPTION);
	
	private Restriction restriction;
	
	private ComponentStateRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
