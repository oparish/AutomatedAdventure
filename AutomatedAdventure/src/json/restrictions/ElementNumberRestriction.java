package json.restrictions;

public enum ElementNumberRestriction implements RestrictionPointer
{
	MAX_VALUE(Restriction.MAX_VALUE), MIN_VALUE(Restriction.MIN_VALUE), MULTIPLIER(Restriction.MULTIPLIER);

	private Restriction restriction;
	
	private ElementNumberRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}
	
	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
