package json.restrictions;

public enum TemplateRestriction implements RestrictionPointer
{
	CONTENT(Restriction.CONTENT), CHANCE_NAME(Restriction.CHANCE_NAME), CONDITIONS(Restriction.CONDITIONS);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private TemplateRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private TemplateRestriction(Restriction restriction)
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
