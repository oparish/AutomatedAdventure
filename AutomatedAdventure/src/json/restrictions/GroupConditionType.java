package json.restrictions;

public enum GroupConditionType {
	FACTION_CONTEST("factionContest"), FACTION_CONFLICT_CHECK("factionConflictCheck");
	
	String name;
	
	private GroupConditionType(String name)
	{
		this.name =  name;
	}
	
	public static GroupConditionType getByName(String name)
	{
		for (GroupConditionType groupConditionType : GroupConditionType.values())
		{
			if (groupConditionType.name.equals(name))
			{
				return groupConditionType;
			}
		}
		return null;
	}
}
