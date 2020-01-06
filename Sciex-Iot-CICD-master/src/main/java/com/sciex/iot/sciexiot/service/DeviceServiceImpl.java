package com.sciex.iot.sciexiot.service;

import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.model.Action;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.AttachThingPrincipalRequest;
import com.amazonaws.services.iot.model.AttributePayload;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.amazonaws.services.iot.model.CreatePolicyRequest;
import com.amazonaws.services.iot.model.CreateThingRequest;
import com.amazonaws.services.iot.model.CreateThingResult;
import com.amazonaws.services.iot.model.CreateTopicRuleRequest;
import com.amazonaws.services.iot.model.CreateTopicRuleResult;
import com.amazonaws.services.iot.model.DynamoDBAction;
import com.amazonaws.services.iot.model.DynamoDBv2Action;
import com.amazonaws.services.iot.model.DynamoKeyType;
import com.amazonaws.services.iot.model.PutItemInput;
import com.amazonaws.services.iot.model.SnsAction;
import com.amazonaws.services.iot.model.TopicRulePayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.sciex.iot.sciexiot.domain.Thing;
import com.sciex.iot.sciexiot.utils.IotClient;

@Service
public class DeviceServiceImpl implements DeviceService {


	@Value("${awsCredentials.access_key_id}")
	private String accessKeyId;

	@Value("${awsCredentials.secret_access_key}")
	private String secretAccessKey;

	@Value("${awsCredentials.client-end-point}")
	private String clientEndPoint;

	@Override
	public CreateThingResult createDevice(String thing) {
		BasicAWSCredentials credential = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		AWSIotClient client = new AWSIotClient(credential);
		client.setRegion(Region.getRegion(Regions.US_WEST_2));
		CreateThingRequest ct = new CreateThingRequest().withThingName(thing);
		CreateThingResult cresult = client.createThing(ct);
		return cresult;
	}

	@Override
	public CreateTopicRuleResult createTopic(String device) {
		try {

			BasicAWSCredentials credential = new BasicAWSCredentials(accessKeyId, secretAccessKey);
			AWSIotClient client = new AWSIotClient(credential);
			client.setRegion(Region.getRegion(Regions.US_WEST_2));
			CreateTopicRuleRequest another_test = new CreateTopicRuleRequest();
			another_test.setRuleName(device);
			TopicRulePayload topicRulePayload = new TopicRulePayload();
			topicRulePayload.setDescription("rule testing");
			topicRulePayload.setSql(
					"SELECT state.reported.sensor FROM '$aws/things/Testing/shadow/update/accepted' WHERE state.reported.sensor=13");
			ArrayList<Action> actionList = new ArrayList<Action>();
			Action action = new Action();
			SnsAction sns = new SnsAction();
			sns.setRoleArn("arn:aws:iam::713829872239:role/service-role/RuleRole");
			sns.setTargetArn("arn:aws:sns:us-west-2:713829872239:ovenRuleTopic");
			action.setSns(sns);
			actionList.add(action);
			topicRulePayload.setActions(actionList);
			another_test.setTopicRulePayload(topicRulePayload);
			CreateTopicRuleResult topicRule = client.createTopicRule(another_test);
			System.out.println("topicRule=="+topicRule);
			return topicRule;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public String updateDevice(String data, String deviceName) {
		try {

			AWSIotMqttClient client = IotClient.getClient(accessKeyId, secretAccessKey, clientEndPoint);

			AWSIotDevice mdevice = new AWSIotDevice(deviceName);

			client.attach(mdevice);
			client.connect();
			
			mdevice.update(data);

			return mdevice.get();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "exception occured";
		}
	}

	@Override
	public CreateThingResult createThing(Thing thing) {
		UUID uniqueId = UUID.randomUUID();
		JsonNode policyJson = thing.getPolicyJson();
		// STEP 1: Create thing ;
		CreateThingResult createThingResult = IotClient.getIotClient(accessKeyId, secretAccessKey)
				.createThing(new CreateThingRequest().withThingName(thing.getThingName()).withAttributePayload(
						new AttributePayload().addAttributesEntry(uniqueId.toString(), thing.getThingName())));

		// STEP 2: Create certificate
		CreateKeysAndCertificateResult createKeysAndCertificateResult = IotClient
				.getIotClient(accessKeyId, secretAccessKey)
				.createKeysAndCertificate(new CreateKeysAndCertificateRequest().withSetAsActive(true));

		String accountId = getAccountIdFromARN(createThingResult.getThingArn());
		// STEP 3: Create policy
		if (policyJson != null) {
			IotClient.getIotClient(accessKeyId, secretAccessKey).createPolicy(new CreatePolicyRequest()
					.withPolicyDocument(policyJson.toString()).withPolicyName(uniqueId.toString() + "-Policy"));
		} else {
			IotClient.getIotClient(accessKeyId, secretAccessKey)
					.createPolicy(new CreatePolicyRequest()
							.withPolicyDocument(getDefaultPolicyDocument(accountId, thing.getThingName()))
							.withPolicyName(uniqueId.toString() + "-Policy"));
		}

		// STEP 4: Attach policy to certificate
		IotClient.getIotClient(accessKeyId, secretAccessKey)
				.attachPrincipalPolicy(new AttachPrincipalPolicyRequest()
						.withPrincipal(createKeysAndCertificateResult.getCertificateArn())
						.withPolicyName(uniqueId.toString() + "-Policy"));

		// STEP 5: Attach thing to certificate
		IotClient.getIotClient(accessKeyId, secretAccessKey).attachThingPrincipal(new AttachThingPrincipalRequest()
				.withPrincipal(createKeysAndCertificateResult.getCertificateArn()).withThingName(thing.getThingName()));

		return createThingResult;
	}
	private String getAccountIdFromARN(String arn) {
		return StringUtils.contains(arn, ":") ? arn.split(":")[4] : "";
	}
	
	private String getDefaultPolicyDocument(String accountId, String thingName) {
		return "{"
			+ "  \"Version\": \"2012-10-17\","
			+ "  \"Statement\": ["
			+ "    {"
			+ "      \"Effect\": \"Allow\","
			+ "      \"Action\": [\"iot:Connect\"],"
			+ "      \"Resource\": [\"*\"]"
			+ "    },"
			+ "    {"
			+ "      \"Effect\": \"Allow\","
			+ "      \"Action\": [\"iot:Publish\"],"
			+ "      \"Resource\": ["
			+ "        \"arn:aws:iot:" + Regions.US_WEST_2 + ":" + accountId + ":topic/$aws/things/" + thingName + "/*\""
			+ "      ]"
			+ "    },"
			+ "    {"
			+ "      \"Effect\": \"Allow\","
			+ "      \"Action\": [\"iot:Receive\", \"iot:Subscribe\"],"
			+ "      \"Resource\": [\"*\"]"
			+ "    },"
			+ "    {"
			+ "      \"Effect\": \"Allow\","
			+ "      \"Action\": ["
			+ "        \"iot:UpdateThingShadow\","
			+ "        \"iot:GetThingShadow\""
			+ "      ],"
			+ "      \"Resource\": [\"arn:aws:iot:" + Regions.US_WEST_2 + ":" + accountId + ":thing/" + thingName + "\"]"
			+ "    }"
			+ "  ]"
			+ "}";
	}

	@Override
	public CreateTopicRuleResult createDynamoDbRuleTopic(com.sciex.iot.sciexiot.domain.Action action) {
		try {
			BasicAWSCredentials credential = new BasicAWSCredentials(accessKeyId, secretAccessKey);
		AWSIotClient client = new AWSIotClient(credential);
		client.setRegion(Region.getRegion(Regions.US_WEST_2));
			   CreateTopicRuleRequest another_test = new CreateTopicRuleRequest();
			   another_test.setRuleName(action.getRuleName());
			   TopicRulePayload topicRulePayload = new TopicRulePayload();
			   topicRulePayload.setDescription(action.getTopicRulePayloadDescription());
				topicRulePayload.setSql(action.getTopicRulePayloadSql());
			   ArrayList<Action> actionList = new ArrayList<Action>();
			   Action actions = new Action();
			   DynamoDBAction db = new DynamoDBAction();
			   db.setTableName(action.getDynamoDbAction().getTableName());
			   db.setHashKeyField(action.getDynamoDbAction().getHashKeyField());
			   db.setHashKeyValue(action.getDynamoDbAction().getHashKeyValue());
			   db.setRangeKeyField(action.getDynamoDbAction().getRangeKeyField());
			   db.setRangeKeyValue(action.getDynamoDbAction().getRangeKeyValue());
			   db.setPayloadField(action.getDynamoDbAction().getPayloadField());		   
			   db.setRoleArn(action.getRoleArn());
			   actions.setDynamoDB(db);
			   actionList.add(actions);
			   topicRulePayload.setActions(actionList);
			   another_test.setTopicRulePayload(topicRulePayload);
				CreateTopicRuleResult topicRule = client.createTopicRule(another_test);
				return topicRule;			   
			}catch(Exception e) {
				e.printStackTrace();
			}
		return null;
	}

	
}
