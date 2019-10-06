package json.restrictions;

public enum ElementRestriction implements RestrictionPointer
{
	ELEMENT_DATA(Restriction.ELEMENT_DATA), INSTANCE_NUMBER(Restriction.INSTANCE_NUMBER);
	
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
