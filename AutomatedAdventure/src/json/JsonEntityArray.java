package json;

import java.util.ArrayList;

public class JsonEntityArray implements JsonEntity
{
	ArrayList<JsonEntity> entityList;
	
	public JsonEntityArray(ArrayList<JsonEntity> entityList)
	{
		this.entityList = entityList;
	}
	
	public JsonEntity getEntityAt(int index)
	{
		return this.entityList.get(index);
	}

	@Override
	public String renderAsString()
	{
		boolean first = true;
		StringBuilder stringBuilder = new StringBuilder("[");
		for(JsonEntity jsonEntity : this.entityList)
		{
			if (first)
			{
				first = false;
			}
			else
			{
				stringBuilder.append(", ");
			}
			stringBuilder.append(jsonEntity.renderAsString());
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
}
