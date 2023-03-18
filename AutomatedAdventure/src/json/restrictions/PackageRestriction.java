package json.restrictions;

public enum PackageRestriction implements RestrictionPointer
{
	REDIRECTS(Restriction.REDIRECTS), RANDOM_REDIRECTS(Restriction.RANDOM_REDIRECTS), PAGES(Restriction.PAGES);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private PackageRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private PackageRestriction(Restriction restriction)
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