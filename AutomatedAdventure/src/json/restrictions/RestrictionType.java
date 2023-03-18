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
	CONTEXT_CHANGE(ContextChangeRestriction.class),
	ELEMENTDATA(ElementDataRestriction.class),
	MAKE_ELEMENT(MakeElementRestriction.class),
	REMOVE_ELEMENT(RemoveElementRestriction.class),
	MAKE_CONNECTION(MakeConnectionRestriction.class),
	ADJUSTMENT_DATA(AdjustmentDataRestriction.class),
	TARGET_IDENTIFICATION(TargetIdentificationRestriction.class),
	ELEMENT_ADJUSTMENT(ElementAdjustmentRestriction.class),
	POSITION_ADJUSTMENT(PositionAdjustmentRestriction.class),
	COUNTER_ADJUSTMENT(CounterAdjustmentRestriction.class),
	COUNTER_INITIALISATION(CounterInitialisationRestriction.class),
	ELEMENT_NUMBER(ElementNumberRestriction.class),
	ELEMENT_CONDITION(ElementConditionRestriction.class),
	CONTEXT_CONDITION(ContextConditionRestriction.class),
	ELEMENT(ElementRestriction.class),
	ELEMENT_SET(ElementSetRestriction.class),
	ELEMENT_SET_MEMBER(ElementSetMemberRestriction.class),
	ELEMENT_CHOICE(ElementChoiceRestriction.class),
	GROUP_CHOICE(GroupChoiceRestriction.class),
	INTERVAL(IntervalRestriction.class),
	IMAGE(ImageRestriction.class),
	TOOLTIP(TooltipRestriction.class),
	TOOLTIP_COMPONENT(TooltipComponentRestriction.class),
	RULE(RuleRestriction.class),
	STATE(StateRestriction.class),
	ROOM(RoomRestriction.class),
	PAGE(PageRestriction.class),
	MAP(MapRestriction.class),
	MAP_ELEMENT(MapElementRestriction.class),
	MAP_POSITION(MapPositionRestriction.class),
	SUM_COMPONENT(SumComponentRestriction.class),
	SUM(SumRestriction.class),
	REDIRECT(RedirectRestriction.class),
	RANDOM_REDIRECT(RandomRedirectRestriction.class),
	CHOICE(ChoiceRestriction.class),
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
	PANEL(PanelRestriction.class),
	INSTANCE_DETAILS(InstanceDetailsRestriction.class),
	CONNECTION(ConnectionRestriction.class),
	PACKAGE(PackageRestriction.class);
	
	private final Class<? extends RestrictionPointer> clazz;
	
	public Class<? extends RestrictionPointer> getClazz() {
		return clazz;
	}

	private RestrictionType(Class<? extends RestrictionPointer> clazz)
	{
		this.clazz = clazz;
	}
}
