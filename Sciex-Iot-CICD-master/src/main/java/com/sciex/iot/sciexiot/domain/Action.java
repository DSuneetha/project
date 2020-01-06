package com.sciex.iot.sciexiot.domain;


public class Action {
private String ruleName;
private String roleArn;
private String topicRulePayloadDescription;
private String topicRulePayloadSql;

private DynamodbAction dynamoDbAction;

public String getRuleName() {
	return ruleName;
}
public void setRuleName(String ruleName) {
	this.ruleName = ruleName;
}
public String getRoleArn() {
	return roleArn;
}
public void setRoleArn(String roleArn) {
	this.roleArn = roleArn;
}
public DynamodbAction getDynamoDbAction() {
	return dynamoDbAction;
}
public void setDynamoDbAction(DynamodbAction dynamoDbAction) {
	this.dynamoDbAction = dynamoDbAction;
}
public String getTopicRulePayloadDescription() {
	return topicRulePayloadDescription;
}
public void setTopicRulePayloadDescription(String topicRulePayloadDescription) {
	this.topicRulePayloadDescription = topicRulePayloadDescription;
}
public String getTopicRulePayloadSql() {
	return topicRulePayloadSql;
}
public void setTopicRulePayloadSql(String topicRulePayloadSql) {
	this.topicRulePayloadSql = topicRulePayloadSql;
}


}
