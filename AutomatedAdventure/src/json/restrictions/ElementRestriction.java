package json.restrictions;

public enum ElementRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), OPTIONS(Restriction.OPTIONS);
	
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
