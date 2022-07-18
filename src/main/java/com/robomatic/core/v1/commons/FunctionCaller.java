package com.robomatic.core.v1.commons;

import com.robomatic.core.v1.exceptions.BadGatewayException;
import com.robomatic.core.v1.exceptions.BadRequestException;
import com.robomatic.core.v1.exceptions.ForbiddenException;
import com.robomatic.core.v1.exceptions.HttpException;
import com.robomatic.core.v1.exceptions.InternalErrorException;
import com.robomatic.core.v1.exceptions.NotFoundException;
import com.robomatic.core.v1.models.ErrorGateway;
import com.robomatic.core.v1.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

import static com.robomatic.core.v1.exceptions.messages.InternalErrorCode.E500000;

@Component
@Slf4j
public class FunctionCaller {

    public ResponseEntity<Object> callFunction(final Object object, final UnaryOperator<Object> function, HttpStatus httpStatus) {
        ErrorGateway error;
        try {
            return new ResponseEntity<>(function.apply(object), httpStatus);
        } catch (InternalErrorException ex) {
            log.info(LogUtils.formatObjectToJson("Internal error", ex.getMessage()));
            log.error("InternalErrorException: ", ex);
            error = ErrorGateway.builder().code(ex.getCode()).message(ex.getMessage()).build();
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (BadRequestException ex) {
            log.info(LogUtils.formatObjectToJson("Bad request error.", ex.getMessage()));
            log.error("BadRequestException: ", ex);
            error = ErrorGateway.builder().code(ex.getCode()).message(ex.getMessage()).build();
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } catch (BadGatewayException ex) {
            log.info(LogUtils.formatObjectToJson("Bad gateway error", ex.getMessage()));
            log.error("BadGatewayException: ", ex);
            error = ErrorGateway.builder().code(ex.getCode()).message(ex.getMessage()).build();
            return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
        } catch (NotFoundException ex) {
            log.info(LogUtils.formatObjectToJson("Not found error", ex.getMessage()));
            log.error("NotFoundException: ", ex);
            error = ErrorGateway.builder().code(ex.getCode()).message(ex.getMessage()).build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        } catch (ForbiddenException ex) {
            log.info(LogUtils.formatObjectToJson("Forbidden error", ex.getMessage()));
            log.error("ForbiddenException: ", ex);
            error = ErrorGateway.builder().code(ex.getCode()).message(ex.getMessage()).build();
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        } catch (HttpException ex) {
            log.info(LogUtils.formatObjectToJson("Http error", ex.getMessage()));
            log.error("HttpException: ", ex);
            error = ErrorGateway.builder().code(ex.getCode()).message(ex.getMessage()).build();
            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        } catch (Exception ex) {
            log.error(LogUtils.formatObjectToJson("Generic error", ex.getMessage()));
            log.error("An error occurred: ", ex);
            error = new ErrorGateway(E500000);
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
