package json.restrictions;

public enum StateRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), RULES(Restriction.RULES);
	
	private Restriction restriction;
	
	private StateRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
