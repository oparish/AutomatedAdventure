package json.restrictions;

public enum ElementDataRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), OPTIONS(Restriction.OPTIONS), UNIQUE(Restriction.UNIQUE);
	
	private Restriction restriction;
	
	private ElementDataRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
