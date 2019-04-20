package json.restrictions;

public enum ConditionRestriction implements RestrictionPointer
{
	TYPE(Restriction.TYPE), VALUE(Restriction.VALUE);
	
	private Restriction restriction;
	
	private ConditionRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
