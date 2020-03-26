package json.restrictions;

public enum ElementRestriction implements RestrictionPointer
{
	ELEMENT_DATA(Restriction.ELEMENT_DATA);
	
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
