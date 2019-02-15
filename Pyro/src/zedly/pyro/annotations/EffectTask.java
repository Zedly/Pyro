package zedly.pyro.annotations;

import zedly.pyro.TaskRunner;
import zedly.pyro.enums.Frequency;

import java.lang.annotation.*;

/**
 * Method annotation used by {@link TaskRunner} to control frequency of execution of scheduled
 * events. Annotations must only be on static methods.
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EffectTask {
	Frequency value();
}
