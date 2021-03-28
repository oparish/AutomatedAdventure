package json.restrictions;

public enum TooltipRestriction implements RestrictionPointer
{
	TOOLTIP_COMPONENTS(Restriction.TOOLTIP_COMPONENTS);	

	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private TooltipRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private TooltipRestriction(Restriction restriction)
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
