package json.restrictions;

import json.JsonType;
import json.restrictions.component.ComponentChangeRestriction;
import json.restrictions.component.ComponentRestriction;
import json.restrictions.component.ComponentStateRestriction;
import json.restrictions.component.EndingRestriction;
import json.restrictions.component.TriggerRestriction;
import json.restrictions.component.ConnectionRestriction;
import json.restrictions.room.ChallengeRoomRestriction;
import json.restrictions.room.RoomRestriction;
import json.restrictions.room.TimedRoomRestriction;

public enum RestrictionType implements JsonType
{
	ELEMENTDATA(ElementDataRestriction.class),
	ELEMENT_NUMBER(ElementNumberRestriction.class),
	ELEMENT(ElementRestriction.class),
	ELEMENT_SET(ElementSetRestriction.class),
	ELEMENT_SET_MEMBER(ElementSetMemberRestriction.class),
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
	CHALLENGEROOM(ChallengeRoomRestriction.class),
	TIMEDROOM(TimedRoomRestriction.class),
	COMPONENT(ComponentRestriction.class),
	ENDING(EndingRestriction.class),
	TRIGGER(TriggerRestriction.class),
	CONNECTION(ConnectionRestriction.class);
	
	private final Class<? extends RestrictionPointer> clazz;
	
	public Class<? extends RestrictionPointer> getClazz() {
		return clazz;
	}

	private RestrictionType(Class<? extends RestrictionPointer> clazz)
	{
		this.clazz = clazz;
	}
}
