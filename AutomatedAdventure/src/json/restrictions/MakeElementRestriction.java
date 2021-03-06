package json.restrictions;

public enum MakeElementRestriction implements RestrictionPointer 
{
	ELEMENT_NAME(Restriction.ELEMENT_NAME), NUMBER_VALUE(Restriction.NUMBER_VALUE), INSTANCE_DETAILS(Restriction.INSTANCE_DETAILS);
	
	private Restriction restriction;
	
	private MakeElementRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
