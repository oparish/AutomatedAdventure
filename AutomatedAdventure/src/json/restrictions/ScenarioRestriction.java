package json.restrictions;

public enum ScenarioRestriction implements RestrictionPointer
{
	ROOMS(Restriction.ROOMS), STATES(Restriction.STATES), INTERVALS(Restriction.INTERVALS), ELEMENTS(Restriction.ELEMENTS), 
	CHECKTIME(Restriction.CHECKTIME), CHANCES(Restriction.CHANCES), MODE(Restriction.MODE), ACTION_TYPES(Restriction.ACTION_TYPES),
	COMPONENTS(Restriction.COMPONENTS), ENDINGS(Restriction.ENDINGS), PAGES(Restriction.PAGES), 
	CONNECTIONS(Restriction.CONNECTIONS), REDIRECTS(Restriction.REDIRECTS), RANDOM_REDIRECTS(Restriction.RANDOM_REDIRECTS), 
	PANELS(Restriction.PANELS), MAPS(Restriction.MAPS);
	
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
