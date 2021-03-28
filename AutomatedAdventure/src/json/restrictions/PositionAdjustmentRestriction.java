package json.restrictions;

public enum PositionAdjustmentRestriction implements RestrictionPointer
{
	MAP_NAME(Restriction.MAP_NAME), ELEMENT_NAME(Restriction.ELEMENT_NAME);
	
	private Restriction restriction;
	
	private PositionAdjustmentRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
