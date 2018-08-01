package me.jinky.fwk;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	String name();

	String permission() default "";

	String noPerm() default "§8[§cCenix§8] §7You don't have permission to do this!";

	String[] aliases() default {};

	String description() default "";

	String usage() default "";
}
