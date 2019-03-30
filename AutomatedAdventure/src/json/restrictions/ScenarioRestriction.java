package json.restrictions;

public enum ScenarioRestriction implements RestrictionPointer
{
	ROOMS(Restriction.ROOMS), STATES(Restriction.STATES), INTERVALS(Restriction.INTERVALS);
	
	private Restriction restriction;
	
	private ScenarioRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
