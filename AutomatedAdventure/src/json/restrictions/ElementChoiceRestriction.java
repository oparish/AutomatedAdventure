package json.restrictions;

public enum ElementChoiceRestriction implements RestrictionPointer
{
	ELEMENT_NAME(Restriction.ELEMENT_NAME), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY), 
	SECOND(Restriction.SECOND), ELEMENT_CONDITIONS(Restriction.ELEMENT_CONDITIONS);
	
	private Restriction restriction;
	
	private ElementChoiceRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
