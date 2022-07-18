package com.robomatic.core.v1.utils;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Trace {
	private String title;
	private Long orderId;
  private String virtualId;
  private String referenceId;
	private String message;
	private Map<String, Object> customs;
	private String traceError;	
}
