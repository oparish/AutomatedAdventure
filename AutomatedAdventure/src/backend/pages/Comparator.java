package backend.pages;

public enum Comparator
{
	GREATER_THAN(">"),
	GREATER_THAN_OR_EQUAL(">="),
	LESS_THAN("<"),
	LESS_THAN_OR_EQUAL("<="),
	EQUAL("=");
	
	private String text;
	
	private Comparator(String text)
	{
		this.text = text;
	}
	
	public static Comparator fromText(String comparatorText) throws Exception
	{
		for (Comparator comparator : Comparator.values())
		{
			if (comparator.text.equals(comparatorText))
				return comparator;
		}
		
		throw new Exception("Unrecognised comparator type.");
	}
}
