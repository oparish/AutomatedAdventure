package backend.pages;

public enum ElementChoiceType
{
	MENU("menu"), MENU_RANGE("menuRange"), ROUTE_SELECTION("routeSelection");
	
	String name;
	
	private ElementChoiceType(String name)
	{
		this.name = name;
	}
	
	public static ElementChoiceType getByName(String name) throws Exception
	{
		for (ElementChoiceType elementChoiceType : ElementChoiceType.values())
		{
			if (elementChoiceType.name.equals(name))
			{
				return elementChoiceType;
			}
		}
		throw new Exception("Unrecognised element choice type: " + name);
	}
}