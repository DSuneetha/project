package com.sciex.iot.sciexiot.service;


import com.amazonaws.services.iot.model.CreateThingResult;
import com.amazonaws.services.iot.model.CreateTopicRuleResult;
import com.sciex.iot.sciexiot.domain.Action;
import com.sciex.iot.sciexiot.domain.Thing;

public interface DeviceService {
	public CreateThingResult createDevice(String device);

	public CreateTopicRuleResult createTopic(String device);

	public CreateTopicRuleResult createDynamoDbRuleTopic(Action action);

	public String updateDevice(String data, String deviceName);

	public CreateThingResult createThing(Thing thing);
}
