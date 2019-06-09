package json.restrictions;

public class TriggerRestriction implements RestrictionPointer
{
	
	
	private Restriction restriction;
	
	private TriggerRestriction(Restriction restriction)
	{
		this.restriction = restriction;
	}

	@Override
	public Restriction getRestriction()
	{
		return this.restriction;
	}

}
