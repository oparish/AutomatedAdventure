package json.restrictions;

public enum ContextChangeRestriction implements RestrictionPointer
{
	COUNTER_TO_GROUP(Restriction.COUNTER_TO_GROUP, true), ADD_ELEMENT_TO_CONTEXT(Restriction.ADD_ELEMENT_TO_CONTEXT, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ContextChangeRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ContextChangeRestriction(Restriction restriction)
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
