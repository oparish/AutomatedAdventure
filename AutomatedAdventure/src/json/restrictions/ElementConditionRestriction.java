package json.restrictions;

public enum ElementConditionRestriction implements RestrictionPointer
{
	TYPE(Restriction.TYPE), NUMBER_VALUE(Restriction.NUMBER_VALUE), ELEMENT_QUALITY(Restriction.ELEMENT_QUALITY);
	
	private Restriction restriction;
	
	private ElementConditionRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
