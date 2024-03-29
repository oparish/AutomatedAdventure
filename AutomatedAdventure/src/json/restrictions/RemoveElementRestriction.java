package json.restrictions;

public enum RemoveElementRestriction implements RestrictionPointer
{
	TARGET_IDENTIFICATION(Restriction.TARGET_IDENTIFICATION), ELEMENT_CONDITIONS(Restriction.ELEMENT_CONDITIONS, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private RemoveElementRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private RemoveElementRestriction(Restriction restriction)
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
