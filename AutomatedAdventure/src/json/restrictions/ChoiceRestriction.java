package json.restrictions;

public enum ChoiceRestriction implements RestrictionPointer
{
	VALUE(Restriction.VALUE), FIRST(Restriction.FIRST), ELEMENT_CHOICE(Restriction.ELEMENT_CHOICE), WITH_CONTEXT(Restriction.WITH_CONTEXT),
	CONTEXT_CONDITION(Restriction.CONTEXT_CONDITION); 
	
	private Restriction restriction;
	
	private ChoiceRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
