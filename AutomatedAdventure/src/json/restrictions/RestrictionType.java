package json.restrictions;

import json.JsonType;

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
	CHALLENGEROOMRESTRICTION(ChallengeRoomRestriction.class),
	TIMEDROOMRESTRICTION(TimedRoomRestriction.class);
	
	private final Class<? extends RestrictionPointer> clazz;
	
	public Class<? extends RestrictionPointer> getClazz() {
		return clazz;
	}

	private RestrictionType(Class<? extends RestrictionPointer> clazz)
	{
		this.clazz = clazz;
	}
}
