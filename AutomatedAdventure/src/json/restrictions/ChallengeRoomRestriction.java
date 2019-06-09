package json.restrictions;

public enum ChallengeRoomRestriction implements RestrictionPointer
{
	ACTION_NAME(Restriction.ACTION_NAME);
	
	private Restriction restriction;
	
	private ChallengeRoomRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
