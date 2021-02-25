package json.restrictions;

public enum MapPositionRestriction implements RestrictionPointer
{
	X(Restriction.X), Y(Restriction.Y);
	
	private Restriction restriction;
	
	private MapPositionRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
