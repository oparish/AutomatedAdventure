package backend;

public enum Faction
{
	PLAYER, COMPUTER;
	
	public static Faction match(String string)
	{
		return Faction.valueOf(string.toUpperCase());
		
	}
}
