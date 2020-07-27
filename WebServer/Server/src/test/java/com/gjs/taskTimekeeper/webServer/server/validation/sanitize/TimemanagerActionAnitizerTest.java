package com.gjs.taskTimekeeper.webServer.server.validation.sanitize;

import com.gjs.taskTimekeeper.baseCode.core.crudAction.ActionConfig;
import com.gjs.taskTimekeeper.webServer.server.testResources.TestResourceLifecycleManager;
import com.gjs.taskTimekeeper.webServer.webLibrary.pojo.timeManager.action.TimeManagerActionRequest;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
@QuarkusTestResource(TestResourceLifecycleManager.class)
class TimemanagerActionAnitizerTest extends AnitizerTest<TimeManagerActionRequest> {
	
	public TimemanagerActionAnitizerTest(TimeManagerActionAnitizer anitizer) {
		super(anitizer);
	}
	
	private static Object[][] args() {
		return new Object[][]{
			new Object[]{null, null},
			new Object[]{
				new TimeManagerActionRequest(new ActionConfig(), 0),
				new TimeManagerActionRequest(new ActionConfig(), 0)
			},
			new Object[]{
				new TimeManagerActionRequest(
					new ActionConfig()
						.setSpecialAction("hello")
						.setName("name")
						.setNewName("newName")
						.setAttributeName("attribute")
						.setNewAttributeName("newAttName")
						.setAttributeVal("attributeVal")
						.setNewAttributeVal("newAttVal")
						.setBefore("before")
						.setAfter("after")
						.setAt("at")
						.setStart("start")
						.setEnd("end")
						.setAttributes("attributes"),
					0
				),
				new TimeManagerActionRequest(
					new ActionConfig()
						.setSpecialAction("hello")
						.setName("name")
						.setNewName("newName")
						.setAttributeName("attribute")
						.setNewAttributeName("newAttName")
						.setAttributeVal("attributeVal")
						.setNewAttributeVal("newAttVal")
						.setBefore("before")
						.setAfter("after")
						.setAt("at")
						.setStart("start")
						.setEnd("end")
						.setAttributes("attributes"),
					0
				)
			},
			new Object[]{
				new TimeManagerActionRequest(
					new ActionConfig()
						.setSpecialAction("<hello>")
						.setName("<name>")
						.setNewName("<newName>")
						.setAttributeName("<attribute>")
						.setNewAttributeName("<newAttName>")
						.setAttributeVal("<attributeVal>")
						.setNewAttributeVal("<newAttVal>")
						.setBefore("<before>")
						.setAfter("<after>")
						.setAt("<at>")
						.setStart("<start>")
						.setEnd("<end>")
						.setAttributes("<attributes>"),
					0
				),
				new TimeManagerActionRequest(
					new ActionConfig()
						.setSpecialAction("&lt;hello&gt;")
						.setName("&lt;name&gt;")
						.setNewName("&lt;newName&gt;")
						.setAttributeName("&lt;attribute&gt;")
						.setNewAttributeName("&lt;newAttName&gt;")
						.setAttributeVal("&lt;attributeVal&gt;")
						.setNewAttributeVal("&lt;newAttVal&gt;")
						.setBefore("&lt;before&gt;")
						.setAfter("&lt;after&gt;")
						.setAt("&lt;at&gt;")
						.setStart("&lt;start&gt;")
						.setEnd("&lt;end&gt;")
						.setAttributes("&lt;attributes&gt;"),
					0
				)
			}
		};
	}
	
	@ParameterizedTest
	@MethodSource("args")
	public void testTimeManagerActionDeSanitizer(
		TimeManagerActionRequest given,
		TimeManagerActionRequest expectedSanitized
	) {
		this.testAnitize(given, expectedSanitized);
	}
}