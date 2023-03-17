package json.restrictions;

public enum MapElementRestriction implements RestrictionPointer
{
	IMAGE(Restriction.IMAGE), TOOLTIP(Restriction.TOOLTIP, true), MAP_ELEMENT_TYPE(Restriction.MAP_ELEMENT_TYPE), 
		FACTION_IDENTIFIER(Restriction.FACTION_IDENTIFIER, true);

	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private MapElementRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private MapElementRestriction(Restriction restriction)
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
