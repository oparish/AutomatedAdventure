package json.restrictions;

public enum InstanceDetailsRestriction implements RestrictionPointer
{
	NAME(Restriction.NAME), NUMBER_MAP(Restriction.NUMBER_MAP), STRING_MAP(Restriction.STRING_MAP), SET_MAP(Restriction.SET_MAP), 
		MAP_MAP(Restriction.MAP_MAP);
	
	private Restriction restriction;
	
	private InstanceDetailsRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
