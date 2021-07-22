package com.aws.sns;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.autoconfigure.context.ContextRegionProviderAutoConfiguration;
import org.springframework.cloud.aws.autoconfigure.context.ContextStackAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.aws.sns.config.CloudProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(exclude = { ContextStackAutoConfiguration.class, ContextRegionProviderAutoConfiguration.class })
@RestController
public class SpringbootAwsSnsExampleApplication {

	@Autowired
	private AmazonSNSClient snsClient;

	@Autowired
	private CloudProperties cloudProperties;

	@GetMapping("/addSubscription/{email}")
	public String addSubscription(@PathVariable String email) {
		SubscribeRequest request = new SubscribeRequest(cloudProperties.getTopicArn(), "email", email);
		snsClient.subscribe(request);
		return "Subscription request is pending. To confirm the subscription, check your email : " + email;
	}

	@GetMapping("/addSubscriptionToMobile/{mobile}")
	public String addSubscriptionToMobile(@PathVariable String mobile) {
		SubscribeRequest request = new SubscribeRequest(cloudProperties.getTopicArn(), "SMS", mobile);
		snsClient.subscribe(request);
		return "Subscription request is pending. To confirm the subscription, check your mobile : " + mobile;
	}

	@GetMapping("/sendNotification")
	public String publishMessageToTopic() {
		PublishRequest publishRequest = new PublishRequest(cloudProperties.getTopicArn(), buildEmailBody(),
				"Notification: Network connectivity issue");
		snsClient.publish(publishRequest);
		log.info("Notification send successfully !!");
		return "Notification send successfully !!";
	}

	private String buildEmailBody() {
		return "Dear User ,\n" + "\n" + "\n" + "Connection Issues." + "\n"
				+ "All the servers in Bangalore Data center are not accessible. We are working on it ! \n"
				+ "Notification will be sent out as soon as the issue is resolved. For any questions regarding this message please feel free to contact IT Service Support team";
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootAwsSnsExampleApplication.class, args);
	}

}
