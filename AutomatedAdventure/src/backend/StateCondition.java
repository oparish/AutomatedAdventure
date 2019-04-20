package backend;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StateCondition extends Condition
{
	State state;
	String stateValue;
	
	private static final Pattern statePattern = Pattern.compile("^(.*?):(.*?)$");
	
	public StateCondition(Scenario scenario, String valueString)
	{
		super();
		Matcher stateMatcher = statePattern.matcher(valueString);
		stateMatcher.find();
		String stateString = stateMatcher.group(1);
		this.stateValue = stateMatcher.group(2);
		this.state = scenario.getStateByName(stateString);
	}

	@Override
	public boolean check(Scenario scenario)
	{
		return this.state.getValue().equals(this.stateValue);
	}
}
