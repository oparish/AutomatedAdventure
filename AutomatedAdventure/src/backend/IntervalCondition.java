package backend;

import java.util.ArrayList;

public class IntervalCondition extends Condition
{
	ArrayList<Interval> intervals = new ArrayList<Interval>();
	
	public IntervalCondition(Scenario scenario, String valueString)
	{
		super();
		String[] intervalStrings = valueString.split("/");
		for (String intervalString : intervalStrings)
		{
			this.intervals.add(scenario.getIntervalByName(intervalString));	
		}
	}

	@Override
	public boolean check(Scenario scenario)
	{
		return this.intervals.contains(scenario.getCurrentInterval());
	}
}
