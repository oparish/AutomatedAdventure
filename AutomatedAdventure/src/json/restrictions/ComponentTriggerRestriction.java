package json.restrictions;

public enum ComponentTriggerRestriction implements RestrictionPointer
{
	COMPONENT_NAME(Restriction.COMPONENT_NAME);
	
	private Restriction restriction;
	
	private ComponentTriggerRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
