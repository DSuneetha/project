package com.sciex.iot.sciexiot.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Thing {

	private String thingName;
	

	@JsonProperty("policyJson")
    private JsonNode policyJson;
	



	public JsonNode getPolicyJson() {
		return policyJson;
	}



	public void setPolicyJson(JsonNode policyJson) {
		this.policyJson = policyJson;
	}




	public String getThingName() {
		return thingName;
	}



	public void setThingName(String thingName) {
		this.thingName = thingName;
	}



	

}
