package json.restrictions;

public enum ElementConditionRestriction implements RestrictionPointer
{
	TYPE(Restriction.TYPE), NUMBER_VALUE(Restriction.NUMBER_VALUE), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY);
	
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
