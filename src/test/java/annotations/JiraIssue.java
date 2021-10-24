package annotations;

import io.qameta.allure.LabelAnnotation;

import java.lang.annotation.*;
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LabelAnnotation(name = "JiraIssue")
public @interface JiraIssue {
    String value();
}
