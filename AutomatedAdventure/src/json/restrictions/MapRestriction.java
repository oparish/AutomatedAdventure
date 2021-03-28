package json.restrictions;

public enum MapRestriction implements RestrictionPointer
{
	WIDTH(Restriction.WIDTH), HEIGHT(Restriction.HEIGHT), TILE_SIZE(Restriction.TILE_SIZE), MAP_ELEMENTS(Restriction.MAP_ELEMENTS), 
	IMAGE(Restriction.IMAGE);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private MapRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private MapRestriction(Restriction restriction)
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
