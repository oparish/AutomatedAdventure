package json.restrictions;

public enum MapElementRestriction implements RestrictionPointer
{
	IMAGE(Restriction.IMAGE), TOOLTIP(Restriction.TOOLTIP), MAP_ELEMENT_TYPE(Restriction.MAP_ELEMENT_TYPE);

	private Restriction restriction;
	
	private MapElementRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}
	
	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
