package backend;

import java.util.HashMap;

public class ImageData extends HashMap<ImageDataKey, String>{
	
	public boolean isEqualTo(ImageData imageData)
	{		
		for (ImageDataKey imageDataKey: ImageDataKey.values())
		{
			String value1 = this.get(imageDataKey);
			String value2 = imageData.get(imageDataKey);
			if ((value1 == null && value2 != null) || (value1 != null && value2 == null))
			{
				return false;
			}	
			else if (!(value1 == null && value2 == null) && !value1.equals(value2))
			{
				return false;
			}
		}
		return true;
	}
}
