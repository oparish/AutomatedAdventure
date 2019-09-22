package backend.component;

import json.RestrictedJson;
import json.restrictions.component.ComponentRestriction;

public class ComponentInstance
{
	RestrictedJson<ComponentRestriction> componentJson;
	public RestrictedJson<ComponentRestriction> getComponentJson() {
		return componentJson;
	}

	ComponentInstance nextComponent;
	
	public ComponentInstance getNextComponent() {
		return nextComponent;
	}

	public void setNextComponent(ComponentInstance nextComponent) {
		this.nextComponent = nextComponent;
	}

	public ComponentInstance(RestrictedJson<ComponentRestriction> componentJson)
	{
		this.componentJson = componentJson;
	}
}
