package json.restrictions;

public enum ElementChoiceRestriction implements RestrictionPointer
{
	ELEMENT_NAME(Restriction.ELEMENT_NAME), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY, true), 
	SECOND(Restriction.SECOND), ELEMENT_CONDITIONS(Restriction.ELEMENT_CONDITIONS, true), TYPE(Restriction.TYPE), 
	RANGE_ATTRIBUTE(Restriction.RANGE_ATTRIBUTE, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ElementChoiceRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ElementChoiceRestriction(Restriction restriction)
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
