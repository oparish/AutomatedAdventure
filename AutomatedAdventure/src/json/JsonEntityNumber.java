package json;

public class JsonEntityNumber implements JsonEntity
{
	public int value;
	
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
