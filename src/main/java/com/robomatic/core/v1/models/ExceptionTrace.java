package cl.multicaja.digital.gateway.v1.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ExceptionTrace {
	private String message;
	private String stackTrace;
}
