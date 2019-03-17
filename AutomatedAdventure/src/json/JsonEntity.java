package json;

import javax.json.JsonObject;

import main.Main;

public interface JsonEntity
{

	public static void main(String[] args)
	{
		JsonObject jsonObject = Main.openJsonFile(null);
		RestrictedJson restrictedJson = new RestrictedJson(jsonObject.getJsonObject("scenario"), Restriction.SCENARIO);
	}
}
