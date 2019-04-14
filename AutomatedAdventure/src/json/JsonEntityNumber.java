package json;

public class JsonEntityNumber implements JsonEntity
{
	private int value;
	
	public int getValue() {
		return value;
	}

	public JsonEntityNumber(int value)
	{
		this.value = value;
	}

	@Override
	public String renderAsString()
	{
		return String.valueOf(this.value);
	}
}
