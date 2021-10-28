package br.com.kbmg.wsmusiccontrol.config.security.annotations;

import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Secured({"ROLE_SYS_ADMIN", "ROLE_SPACE_OWNER"})
public @interface SecuredSpaceOwner {
}
