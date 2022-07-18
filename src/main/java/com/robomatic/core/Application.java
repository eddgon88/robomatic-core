package com.robomatic.core;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableConfigurationProperties
@EnableJms
@EnableRabbit
@Slf4j
@SpringBootApplication
public class Application {

	private static String mask(String text) {
		if (StringUtils.isBlank(text)) {
			return text;
		}
		try {
			String textMask = text;
			if (text.length() > 2) {
				textMask = text.substring(0, text.length() / 2);
				textMask = textMask + StringUtils.rightPad("", text.length() / 2, "*");
			}
			return textMask;
		} catch (Exception ex) {
			log.error("An error occurred: ", ex);
			return text;
		}
	}

	public static void main(String[] args) {
		String ciCommitSha = getenv("CI_COMMIT_SHA");
		String springProfilesActive = getenv("SPRING_PROFILES_ACTIVE");
		String jenkinsBuild = getenv("JENKINS_BUILD");
		String dbHost = mask(getenv("DB_HOST"));
		String dbPort = getenv("DB_PORT");
		String dbUser = mask(getenv("DB_USER"));

		log.info("CI_COMMIT_SHA: {}", ciCommitSha);
		log.info("JENKINS_BUILD: {}", jenkinsBuild);
		log.info("SPRING_PROFILES_ACTIVE: {}", springProfilesActive);
		log.info("DB_HOST: {}", dbHost);
		log.info("DB_PORT: {}", dbPort);
		log.info("DB_USER: {}", dbUser);
		SpringApplication.run(Application.class, args);

	}

	private static String getenv(String variable) {
		return System.getenv(variable);
	}

}
