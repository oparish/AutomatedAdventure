package json.restrictions;

public enum RedirectRestriction implements RestrictionPointer
{
	CONTEXT_CONDITION(Restriction.CONTEXT_CONDITION), FIRST(Restriction.FIRST), SECOND(Restriction.SECOND);
	
	private Restriction restriction;
	
	private RedirectRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
