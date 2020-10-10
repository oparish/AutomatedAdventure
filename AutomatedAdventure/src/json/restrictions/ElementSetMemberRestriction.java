package json.restrictions;

public enum ElementSetMemberRestriction implements RestrictionPointer
{
	OPTIONS(Restriction.OPTIONS);
	
	private Restriction restriction;
	
	private ElementSetMemberRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
