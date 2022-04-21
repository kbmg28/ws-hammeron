package br.com.kbmg.wshammeron.unit.annotation;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(roles = "SPACE_OWNER")
@Test
public @interface WithSpaceOwnerTest {
}
