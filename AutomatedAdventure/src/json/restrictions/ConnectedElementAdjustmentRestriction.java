package json.restrictions;

public enum ConnectedElementAdjustmentRestriction implements RestrictionPointer
{
	CONNECTION_NAME(Restriction.CONNECTION_NAME), ELEMENT_NAME(Restriction.ELEMENT_NAME), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY), 
	NUMBER_VALUE(Restriction.NUMBER_VALUE);
	
	private Restriction restriction;
	
	private ConnectedElementAdjustmentRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
