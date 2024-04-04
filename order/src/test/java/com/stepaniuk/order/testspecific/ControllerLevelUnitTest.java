package com.stepaniuk.order.testspecific;


import com.stepaniuk.order.shared.OrderExceptionHandler;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(properties = "spring.mvc.problem-details.enabled=true")
@Import({ OrderExceptionHandler.class})
@ImportAutoConfiguration
public @interface ControllerLevelUnitTest {

  @AliasFor(attribute = "controllers", annotation = WebMvcTest.class) Class<?>[] controllers() default {};
}
