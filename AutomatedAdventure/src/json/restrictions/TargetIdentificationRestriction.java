package json.restrictions;

public enum TargetIdentificationRestriction implements RestrictionPointer
{
	CONNECTION_NAME(Restriction.CONNECTION_NAME, true), ELEMENT_NAME(Restriction.ELEMENT_NAME), TYPE(Restriction.TYPE),
	COUNTER_NAME(Restriction.COUNTER_NAME, true);

	private Restriction restriction;
	private boolean optional;
	
	private TargetIdentificationRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private TargetIdentificationRestriction(Restriction restriction)
	{
		this.optional = false;
		this.restriction = restriction;
	}
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
