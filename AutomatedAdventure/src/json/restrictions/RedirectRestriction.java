package json.restrictions;

public enum RedirectRestriction implements RestrictionPointer
{
	CONTEXT_CONDITIONS(Restriction.CONTEXT_CONDITIONS), FIRST(Restriction.FIRST), SECOND(Restriction.SECOND), 
	ELEMENT_ADJUSTMENTS(Restriction.ELEMENT_ADJUSTMENTS);
	
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
