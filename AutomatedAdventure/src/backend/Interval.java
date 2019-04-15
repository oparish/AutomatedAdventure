package backend;

public class Interval 
{
	private String name;
	private int time;
	
	public String getName() {
		return name;
	}

	public int getTime() {
		return time;
	}

	public Interval(String name, int time)
	{
		this.name = name;
		this.time = time;
	}
}
