package json.restrictions;

public enum TooltipComponentRestriction implements RestrictionPointer
{
	TOOLTIP_TEXT(Restriction.TOOLTIP_TEXT), CONTEXT_CONDITIONS(Restriction.CONTEXT_CONDITIONS, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private TooltipComponentRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private TooltipComponentRestriction(Restriction restriction)
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
