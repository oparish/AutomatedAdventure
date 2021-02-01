package json.restrictions;

public enum ChanceRestriction implements RestrictionPointer
{
	PERCENTAGE(Restriction.PERCENTAGE), PRIORITY(Restriction.PRIORITY);
		
	private Restriction restriction;
	
	private ChanceRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
