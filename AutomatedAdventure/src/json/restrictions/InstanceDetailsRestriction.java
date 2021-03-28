package json.restrictions;

public enum InstanceDetailsRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), NUMBER_MAP(Restriction.NUMBER_MAP, true), STRING_MAP(Restriction.STRING_MAP, true), SET_MAP(Restriction.SET_MAP, true), 
		MAP_MAP(Restriction.MAP_MAP, true);
	
	private Restriction restriction;
	private boolean optional;
	
	@Override
	public boolean getOptional()
	{
		return this.optional;
	}
	
	private InstanceDetailsRestriction(Restriction restriction, boolean optional)
	{
		this.optional = optional;
		this.restriction = restriction;
	}
	
	private InstanceDetailsRestriction(Restriction restriction)
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
