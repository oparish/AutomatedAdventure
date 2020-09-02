package json.restrictions;

public enum MapRestriction implements RestrictionPointer
{
	WIDTH(Restriction.WIDTH), HEIGHT(Restriction.HEIGHT), TILE_SIZE(Restriction.TILE_SIZE), MAP_ELEMENTS(Restriction.MAP_ELEMENTS), 
	IMAGE(Restriction.IMAGE);
	
	private Restriction restriction;
	
	private MapRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}
	
	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
