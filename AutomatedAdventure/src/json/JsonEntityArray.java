package json;

import java.util.ArrayList;

public class JsonEntityArray implements JsonEntity
{
	ArrayList<JsonEntity> entityList;
	
	public JsonEntityArray(ArrayList<JsonEntity> entityList)
	{
		this.entityList = entityList;
	}
}
