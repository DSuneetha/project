package com.sciex.iot.sciexiot.domain;

public class DynamodbAction {
private String tableName;
private String hashKeyField;
private String hashKeyValue;
private String rangeKeyField;
private String rangeKeyValue;
private String payloadField;
private String operation;

public String getTableName() {
	return tableName;
}
public void setTableName(String tableName) {
	this.tableName = tableName;
}
public String getHashKeyField() {
	return hashKeyField;
}
public void setHashKeyField(String hashKeyField) {
	this.hashKeyField = hashKeyField;
}
public String getHashKeyValue() {
	return hashKeyValue;
}
public void setHashKeyValue(String hashKeyValue) {
	this.hashKeyValue = hashKeyValue;
}
public String getRangeKeyField() {
	return rangeKeyField;
}
public void setRangeKeyField(String rangeKeyField) {
	this.rangeKeyField = rangeKeyField;
}
public String getRangeKeyValue() {
	return rangeKeyValue;
}
public void setRangeKeyValue(String rangeKeyValue) {
	this.rangeKeyValue = rangeKeyValue;
}
public String getPayloadField() {
	return payloadField;
}
public void setPayloadField(String payloadField) {
	this.payloadField = payloadField;
}
public String getOperation() {
	return operation;
}
public void setOperation(String operation) {
	this.operation = operation;
}


}
