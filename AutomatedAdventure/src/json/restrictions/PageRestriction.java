package json.restrictions;

public enum PageRestriction implements RestrictionPointer
{
	VALUE(Restriction.VALUE), CHOICES(Restriction.CHOICES, true), RETURN_TO(Restriction.RETURN_TO, true), 
	PANEL_NAME(Restriction.PANEL_NAME), SECONDARY_PANEL_NAME(Restriction.SECONDARY_PANEL_NAME, true), 
	ADJUSTMENT_DATA(Restriction.ADJUSTMENT_DATA, true);
	
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
