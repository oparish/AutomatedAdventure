package json.restrictions;

public enum ChoiceRestriction implements RestrictionPointer
{
	VALUE(Restriction.VALUE), FIRST(Restriction.FIRST), ELEMENT_CHOICE(Restriction.ELEMENT_CHOICE, true), WITH_CONTEXT(Restriction.WITH_CONTEXT),
	CONTEXT_CONDITIONS(Restriction.CONTEXT_CONDITIONS, true), GROUP_CHOICE(Restriction.GROUP_CHOICE, true);
	
	private boolean optional;
	private Restriction restriction;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private ChoiceRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private ChoiceRestriction(Restriction restriction)
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
