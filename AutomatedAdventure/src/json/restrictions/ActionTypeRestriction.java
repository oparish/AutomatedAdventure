package json.restrictions;

public enum ActionTypeRestriction implements RestrictionPointer
{
	ACTIONS(Restriction.ACTIONS), NAME(Restriction.NAME);

	private Restriction restriction;
	
	
	private ActionTypeRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}
	
	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
