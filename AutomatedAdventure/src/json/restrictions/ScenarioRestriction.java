package json.restrictions;

public enum ScenarioRestriction implements RestrictionPointer
{
	ROOMS(Restriction.ROOMS), STATES(Restriction.STATES), INTERVALS(Restriction.INTERVALS), ELEMENTS(Restriction.ELEMENTS), 
	CHECKTIME(Restriction.CHECKTIME), CHANCES(Restriction.CHANCES), MODE(Restriction.MODE), ACTION_TYPES(Restriction.ACTION_TYPES),
	COMPONENTS(Restriction.COMPONENTS), ENDINGS(Restriction.ENDINGS), PAGES(Restriction.PAGES), 
	CONNECTIONS(Restriction.CONNECTIONS), REDIRECTS(Restriction.REDIRECTS), RANDOM_REDIRECTS(Restriction.RANDOM_REDIRECTS), 
	PANELS(Restriction.PANELS), MAPS(Restriction.MAPS), SUMS(Restriction.SUMS);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ScenarioRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ScenarioRestriction(Restriction restriction)
	{
		this.optional = false;
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
