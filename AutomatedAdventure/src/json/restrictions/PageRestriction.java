package json.restrictions;

public enum PageRestriction implements RestrictionPointer
{
	VALUE(Restriction.VALUE), CHOICES(Restriction.CHOICES), MAKE_ELEMENTS(Restriction.MAKE_ELEMENTS), 
		MAKE_CONNECTIONS(Restriction.MAKE_CONNECTIONS), EACH_ELEMENT_ADJUSTMENTS(Restriction.EACH_ELEMENT_ADJUSTMENTS), 
		SELECTED_ELEMENT_ADJUSTMENTS(Restriction.SELECTED_ELEMENT_ADJUSTMENTS), 
		CONNECTED_ELEMENT_ADJUSTMENTS(Restriction.CONNECTED_ELEMENT_ADJUSTMENTS);
	
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
