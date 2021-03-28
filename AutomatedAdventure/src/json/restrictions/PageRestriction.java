package json.restrictions;

public enum PageRestriction implements RestrictionPointer
{
	VALUE(Restriction.VALUE), CHOICES(Restriction.CHOICES, true), RETURN_TO(Restriction.RETURN_TO, true), 
	MAKE_ELEMENTS(Restriction.MAKE_ELEMENTS, true), MAKE_CONNECTIONS(Restriction.MAKE_CONNECTIONS, true), 
	ELEMENT_ADJUSTMENTS(Restriction.ELEMENT_ADJUSTMENTS, true), PANEL_NAME(Restriction.PANEL_NAME), 
	POSITION_ADJUSTMENTS(Restriction.POSITION_ADJUSTMENTS, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private PageRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private PageRestriction(Restriction restriction)
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
