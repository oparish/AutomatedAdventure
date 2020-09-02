package json.restrictions;

public enum PanelRestriction implements RestrictionPointer
{
	X(Restriction.X), Y(Restriction.Y), WIDTH(Restriction.WIDTH), HEIGHT(Restriction.HEIGHT), MAP_NAME(Restriction.MAP_NAME);
	
	private Restriction restriction;
	
	private PanelRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
