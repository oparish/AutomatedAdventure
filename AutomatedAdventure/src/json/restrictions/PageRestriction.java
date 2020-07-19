package json.restrictions;

public enum PageRestriction implements RestrictionPointer
{
	VALUE(Restriction.VALUE), CHOICES(Restriction.CHOICES), MAKE_ELEMENTS(Restriction.MAKE_ELEMENTS), 
		MAKE_CONNECTIONS(Restriction.MAKE_CONNECTIONS), ELEMENT_ADJUSTMENTS(Restriction.ELEMENT_ADJUSTMENTS), PANEL_NAME(Restriction.PANEL_NAME);
	
	private Restriction restriction;
	
	private PageRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
