package json.restrictions;

public enum PageRestriction implements RestrictionPointer
{
	VALUE(Restriction.VALUE), CHOICES(Restriction.CHOICES);
	
	private Restriction restriction;
	
	private PageRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}
}
