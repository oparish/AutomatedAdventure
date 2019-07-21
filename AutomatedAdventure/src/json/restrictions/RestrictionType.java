package json.restrictions;

import json.JsonType;
import json.restrictions.component.ComponentChangeRestriction;
import json.restrictions.component.ComponentRestriction;
import json.restrictions.component.ComponentStateRestriction;
import json.restrictions.component.ComponentTriggerRestriction;
import json.restrictions.component.TimedTriggerRestriction;
import json.restrictions.component.TriggerRestriction;
import json.restrictions.room.ChallengeRoomRestriction;
import json.restrictions.room.RoomRestriction;
import json.restrictions.room.TimedRoomRestriction;

public enum RestrictionType implements JsonType
{
	ELEMENTDATA(ElementDataRestriction.class),
	ELEMENT(ElementRestriction.class),
	INTERVAL(IntervalRestriction.class),
	RULE(RuleRestriction.class),
	STATE(StateRestriction.class),
	ROOM(RoomRestriction.class),
	SCENARIO(ScenarioRestriction.class),
	CHANCE(ChanceRestriction.class),
	TEMPLATE(TemplateRestriction.class),
	ACTION_TYPE(ActionTypeRestriction.class),
	ACTION(ActionRestriction.class),
	CONDITION(ConditionRestriction.class),
	COMPONENT_STATE(ComponentStateRestriction.class),
	COMPONENT_CHANGE(ComponentChangeRestriction.class),
	TRIGGER(TriggerRestriction.class),
	CHALLENGEROOM(ChallengeRoomRestriction.class),
	TIMEDROOM(TimedRoomRestriction.class),
	TIMEDTRIGGER(TimedTriggerRestriction.class),
	COMPONENTTRIGGER(ComponentTriggerRestriction.class),
	COMPONENT(ComponentRestriction.class);
	
	private final Class<? extends RestrictionPointer> clazz;
	
	public Class<? extends RestrictionPointer> getClazz() {
		return clazz;
	}

	private RestrictionType(Class<? extends RestrictionPointer> clazz)
	{
		this.clazz = clazz;
	}
}
