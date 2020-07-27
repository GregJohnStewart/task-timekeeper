package com.gjs.taskTimekeeper.webServer.server.exception;

import com.gjs.taskTimekeeper.webServer.server.exception.database.DatabaseException;
import com.gjs.taskTimekeeper.webServer.server.exception.database.request.EntityNotFoundException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.RequestException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.TooManyRequestsException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.CorruptedKeyException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.IncorrectPasswordException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.UserLockedException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.UserRegistrationException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.UserRequestException;
import com.gjs.taskTimekeeper.webServer.server.exception.request.user.UserUnauthorizedException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.EmailValidationException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.PasswordValidationException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.UsernameValidationException;
import com.gjs.taskTimekeeper.webServer.server.exception.validation.ValidationException;
import com.gjs.taskTimekeeper.webServer.server.testResources.WebServerTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.ws.rs.core.Response;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class WebserverExceptionTest extends WebServerTest {
	
	public static final String TEST_MESSAGE = "test messssage";
	
	private static Stream<Arguments> paramList() {
		return Stream.of(
			Arguments.of(WebServerException.class, 500),
			Arguments.of(DatabaseException.class, 500),
			Arguments.of(EntityNotFoundException.class, 404),
			Arguments.of(RequestException.class, 400),
			Arguments.of(TooManyRequestsException.class, 429),
			Arguments.of(CorruptedKeyException.class, 400),
			Arguments.of(IncorrectPasswordException.class, 401),
			Arguments.of(UserLockedException.class, 403),
			Arguments.of(UserRegistrationException.class, 400),
			Arguments.of(UserRequestException.class, 400),
			Arguments.of(UserUnauthorizedException.class, 401),
			Arguments.of(EmailValidationException.class, 400),
			Arguments.of(PasswordValidationException.class, 400),
			Arguments.of(UsernameValidationException.class, 400),
			Arguments.of(ValidationException.class, 400)
		);
	}
	
	@ParameterizedTest
	@MethodSource("paramList")
	void baseExceptionTest(
		Class<? extends WebServerException> exceptionClass
	) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		log.debug("Testing: {}", exceptionClass.getName());
		Constructor<? extends WebServerException> constBase = exceptionClass.getConstructor();
		Constructor<? extends WebServerException> constStr =
			exceptionClass.getConstructor(String.class);
		Constructor<? extends WebServerException> constThro =
			exceptionClass.getConstructor(Throwable.class);
		Constructor<? extends WebServerException> constStrThro =
			exceptionClass.getConstructor(String.class, Throwable.class);
		Constructor<? extends WebServerException> constStrThroSupWritable =
			exceptionClass.getConstructor(
				String.class, Throwable.class, boolean.class, boolean.class);
		
		WebServerException curException = constBase.newInstance();
		Throwable testThrowable = new RuntimeException("test throwable");
		
		curException = constStr.newInstance(TEST_MESSAGE);
		assertEquals(TEST_MESSAGE, curException.getMessage());
		
		curException = constThro.newInstance(testThrowable);
		assertEquals(testThrowable, curException.getCause());
		
		curException = constStrThro.newInstance(TEST_MESSAGE, testThrowable);
		assertEquals(TEST_MESSAGE, curException.getMessage());
		assertEquals(testThrowable, curException.getCause());
		
		curException =
			constStrThroSupWritable.newInstance(TEST_MESSAGE, testThrowable, false, false);
		assertEquals(TEST_MESSAGE, curException.getMessage());
		assertEquals(testThrowable, curException.getCause());
	}
	
	@ParameterizedTest
	@MethodSource("paramList")
	void toResponseTest(
		Class<? extends WebServerException> exceptionClass,
		int expectedCode
	) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		log.info("Testing response returned by {}", exceptionClass);
		Constructor<? extends WebServerException> constStr = exceptionClass.getConstructor(String.class);
		
		WebServerException exception = constStr.newInstance(TEST_MESSAGE);
		Response response = exception.toResponse();
		
		assertEquals(expectedCode, response.getStatus());
		assertEquals(TEST_MESSAGE, response.getEntity());
	}
}
