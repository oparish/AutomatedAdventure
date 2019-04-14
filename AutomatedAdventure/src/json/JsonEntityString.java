package json;

public class JsonEntityString implements JsonEntity
{
	private String value;
	
	public JsonEntityString(String value)
	{
		this.value = value;
	}

	@Override
	public String renderAsString()
	{
		return this.value;
	}
}
