package json.restrictions;

public enum ActionRestriction implements RestrictionPointer
{
	SHOWN_NAME(Restriction.SHOWN_NAME), KEY_NAME(Restriction.KEY_NAME);
	
	private Restriction restriction;
	
	private ActionRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
