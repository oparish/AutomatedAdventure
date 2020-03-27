package json;

public class JsonEntityBoolean implements JsonEntity
{
	private boolean value;
	
	@Override
	public String renderAsString()
	{
		return String.valueOf(this.value);
	}

	public boolean isValue()
	{
		return this.value;
	}
	
	public JsonEntityBoolean(boolean value)
	{
		this.value = value;
	}
}
