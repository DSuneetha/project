package com.sciex.iot.sciexiot.utils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.client.AWSIotMqttClient;

public class IotClient {

	private static AWSIotMqttClient client;
	
	private static AWSIot iotClient;


	public static AWSIotMqttClient getClient(String accessKeyId, String secretAccessKey, String clientEndPoint) {
		if (client == null) {
			return new AWSIotMqttClient(clientEndPoint, "1122", accessKeyId, secretAccessKey);
		} else {
			return client;
		}
	}

	public static AWSIotClient getClient(String accessKeyId, String secreteKey) {
		BasicAWSCredentials credential = new BasicAWSCredentials(accessKeyId, secreteKey);
		AWSIotClient client = new AWSIotClient(credential);
		return client;

	}
	
	public static AWSIot getIotClient(String accessKeyId,String secretAccessKey) {
		if (iotClient != null) {
			return iotClient;
		}

		iotClient = AWSIotClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(accessKeyId, secretAccessKey))).
				withRegion(Regions.US_WEST_2).build();

		
		return iotClient;
	}
}
