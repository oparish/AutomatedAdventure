package json.restrictions;

public enum ElementSetRestriction implements RestrictionPointer
{
	MEMBERS(Restriction.MEMBERS);
	
	private Restriction restriction;
	
	private ElementSetRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
