package json.restrictions;

public enum ActionTypeRestriction implements RestrictionPointer
{
	ACTIONS(Restriction.ACTIONS), NAME(Restriction.NAME);

	private boolean optional;
	private Restriction restriction;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ActionTypeRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ActionTypeRestriction(Restriction restriction)
	{
		this.optional = false;
		this.restriction = restriction;
	}
	
	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
