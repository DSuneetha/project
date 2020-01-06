package com.sciex.iot.sciexiot.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.iot.model.CreateThingResult;
import com.amazonaws.services.iot.model.CreateTopicRuleResult;
import com.sciex.iot.sciexiot.domain.Action;
import com.sciex.iot.sciexiot.domain.Thing;
import com.sciex.iot.sciexiot.service.DeviceService;

@RestController
public class DeviceController {

	private DeviceService deviceService;

	public DeviceController(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@PostMapping("/createDevice")
	public ResponseEntity<CreateThingResult> createDevice(@RequestBody String device) {
		if (device == null || StringUtils.isEmpty(device)) {
			return new ResponseEntity<CreateThingResult>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<CreateThingResult>(deviceService.createDevice(device), HttpStatus.CREATED);
	}

	@PostMapping("/createSNSRuleTopic")
	public ResponseEntity<CreateTopicRuleResult> createSNSRuleTopic(@RequestBody String ruleTopic) {
		if (ruleTopic == null || StringUtils.isEmpty(ruleTopic)) {
			return new ResponseEntity<CreateTopicRuleResult>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<CreateTopicRuleResult>(deviceService.createTopic(ruleTopic), HttpStatus.OK);
	}

	@PostMapping("/createDynamoDbRuleTopic")
	public ResponseEntity<CreateTopicRuleResult> createDynamoDbRuleTopic(@RequestBody Action action) {
		if (action == null || StringUtils.isEmpty(action)) {
			return new ResponseEntity<CreateTopicRuleResult>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<CreateTopicRuleResult>(deviceService.createDynamoDbRuleTopic(action),
				HttpStatus.OK);
	}
	@PutMapping("/device/{name}")
	public ResponseEntity<String> updateDevice(@RequestBody String data, @PathVariable(value="name") String deviceName) {
		if (deviceName == null || StringUtils.isEmpty(deviceName)) {
			return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<String>(deviceService.updateDevice(data, deviceName), HttpStatus.CREATED);
	}
	
	@PostMapping("/createThing")
	public ResponseEntity<CreateThingResult> createThing(@RequestBody Thing thing) {
		if (thing == null || StringUtils.isEmpty(thing)) {
			return new ResponseEntity<CreateThingResult>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<CreateThingResult>(deviceService.createThing(thing), HttpStatus.CREATED);
	}
}
