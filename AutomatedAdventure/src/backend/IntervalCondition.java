package backend;

import main.Main;

public class IntervalCondition extends Condition
{
	Interval interval;
	
	public IntervalCondition(Scenario scenario, String valueString)
	{
		super();
		this.interval = scenario.getIntervalByName(valueString);
	}

	@Override
	public boolean check(Scenario scenario)
	{
		return this.interval == scenario.getCurrentInterval();
	}
}
