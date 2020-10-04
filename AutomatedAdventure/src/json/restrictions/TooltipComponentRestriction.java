package json.restrictions;

public enum TooltipComponentRestriction implements RestrictionPointer
{
	TOOLTIP_TEXT(Restriction.TOOLTIP_TEXT), CONTEXT_CONDITIONS(Restriction.CONTEXT_CONDITIONS);
	
	private Restriction restriction;
	
	private TooltipComponentRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}
	
	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
