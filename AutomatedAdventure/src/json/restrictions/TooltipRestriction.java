package json.restrictions;

public enum TooltipRestriction implements RestrictionPointer
{
	TOOLTIP_COMPONENTS(Restriction.TOOLTIP_COMPONENTS);	

	private Restriction restriction;
	
	private TooltipRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}
	
	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
