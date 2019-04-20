package json.restrictions;

public enum TemplateRestriction implements RestrictionPointer
{
	CONTENT(Restriction.CONTENT), CHANCE_NAME(Restriction.CHANCE_NAME), CONDITIONS(Restriction.CONDITIONS);
	
	private Restriction restriction;
	
	private TemplateRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
