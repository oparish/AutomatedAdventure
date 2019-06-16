package json.restrictions;

public enum TriggerRestriction implements RestrictionPointer, SuperRestriction
{
	COMPONENT_STATE_NAME(Restriction.COMPONENT_STATE_NAME);
	
	private Restriction restriction;
	
	private TriggerRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

	@Override
	public RestrictionType[] getSubRestrictions()
	{
		return new RestrictionType[] {RestrictionType.TIMEDTRIGGER, RestrictionType.COMPONENTTRIGGER};
	}

}
