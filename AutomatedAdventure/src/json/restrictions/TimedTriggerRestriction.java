package json.restrictions;

public enum TimedTriggerRestriction implements RestrictionPointer
{
	TIME(Restriction.TIME);
	
	private Restriction restriction;
	
	private TimedTriggerRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
