package json.restrictions;

public enum RoomRestriction implements RestrictionPointer
{
	TEMPLATES(Restriction.TEMPLATES), NAME(Restriction.NAME);
	
	private Restriction restriction;
	
	private RoomRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
