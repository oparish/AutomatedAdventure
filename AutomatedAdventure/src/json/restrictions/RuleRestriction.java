package json.restrictions;

public enum RuleRestriction implements RestrictionPointer
{
	INTERVAL_NAME(Restriction.INTERVAL_NAME), CHANCE(Restriction.CHANCE);
	
	private Restriction restriction;
	
	private RuleRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
