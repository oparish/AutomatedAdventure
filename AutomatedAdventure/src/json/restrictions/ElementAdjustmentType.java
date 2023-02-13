package json.restrictions;

public enum ElementAdjustmentType
{
	CONNECTED, SELECTED, EACH, GROUP, ATPOSITION;
	
	public static ElementAdjustmentType stringToType(String typeString)
	{
		String upperCaseString = typeString.toUpperCase();
		return ElementAdjustmentType.valueOf(upperCaseString);
	}
}
