package json;

import java.util.ArrayList;

public class JsonEntityArray<J extends JsonEntity> implements JsonEntity
{
	ArrayList<J> entityList;
	
	public JsonEntityArray(ArrayList<J> entityList)
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
