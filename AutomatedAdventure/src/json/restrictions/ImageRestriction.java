package json.restrictions;

public enum ImageRestriction implements RestrictionPointer
{
	FILENAME(Restriction.FILENAME);
	
	private Restriction restriction;
	
	private ImageRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
