package json.restrictions;

public enum ElementRestriction implements RestrictionPointer
{
	ELEMENT_DATA(Restriction.ELEMENT_DATA), ELEMENT_NUMBERS(Restriction.ELEMENT_NUMBERS), ELEMENT_SETS(Restriction.ELEMENT_SETS), 
	UNIQUE(Restriction.UNIQUE);
	
	private Restriction restriction;
	
	private ElementRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
