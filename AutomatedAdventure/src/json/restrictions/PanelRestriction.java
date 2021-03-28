package json.restrictions;

public enum PanelRestriction implements RestrictionPointer
{
	X(Restriction.X), Y(Restriction.Y), WIDTH(Restriction.WIDTH), HEIGHT(Restriction.HEIGHT), MAP_NAME(Restriction.MAP_NAME, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private PanelRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private PanelRestriction(Restriction restriction)
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
