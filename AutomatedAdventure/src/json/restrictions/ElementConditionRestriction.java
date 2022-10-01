package json.restrictions;

public enum ElementConditionRestriction implements RestrictionPointer
{
	TYPE(Restriction.TYPE, true), NUMBER_VALUE(Restriction.NUMBER_VALUE, true), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY), 
	QUALITY_NAME(Restriction.QUALITY_NAME, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ElementConditionRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ElementConditionRestriction(Restriction restriction)
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
