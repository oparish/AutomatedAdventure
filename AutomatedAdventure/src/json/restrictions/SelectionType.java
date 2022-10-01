package json.restrictions;

public enum SelectionType {
	SINGLE_SELECT("singleSelect"), FROM_GROUP("fromGroup");
	
	String name;
	
	private SelectionType(String name)
	{
		this.name = name;
	}
	
	public static SelectionType fromString(String name)
	{
		for (SelectionType selectionType : SelectionType.values())
		{
			if (selectionType.name.equals(name))
				return selectionType;
		}
		return null;
	}
}
