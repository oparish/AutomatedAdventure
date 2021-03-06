package json.restrictions;

public enum RandomRedirectRestriction implements RestrictionPointer
{
	NUMBER_MAP(Restriction.NUMBER_MAP), ELEMENT_ADJUSTMENTS(Restriction.ELEMENT_ADJUSTMENTS);
	
	private Restriction restriction;
	
	private RandomRedirectRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
