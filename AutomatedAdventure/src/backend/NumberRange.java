package backend;

public class NumberRange
{
	private int start;
	private int end;
	
	public NumberRange(int start, int end)
	{
		this.start = start;
		this.end = end;
	}
	
	public boolean check(int value)
	{
		return value > this.start && value <= this.end;
	}
}
