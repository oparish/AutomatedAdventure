package json.restrictions;

public enum ContextConditionRestriction implements RestrictionPointer
{
	TYPE(Restriction.TYPE), NUMBER_VALUE(Restriction.NUMBER_VALUE), ELEMENT_NAME(Restriction.ELEMENT_NAME), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY);
	
	private Restriction restriction;
	
	private ContextConditionRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
