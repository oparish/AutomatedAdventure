package backend;

public abstract class Condition 
{
	private enum ConditionType
	{
		INTERVAL, STATE;
	}
	
	public abstract boolean check(Scenario scenario);
	
	public static Condition createCondition(Scenario scenario, String typeString, String conditionString)
	{
		ConditionType type = ConditionType.valueOf(typeString.toUpperCase());
		switch (type)
		{
			case INTERVAL:
				return new IntervalCondition(scenario, conditionString);
			case STATE:
				return new StateCondition(scenario, conditionString);
			default:
				new Exception("Condition type " + typeString + "not recognised.");
				return null;
		}
	}
}
