package br.com.kbmg.wsmusiccontrol.integration.annotations;

import br.com.kbmg.wsmusiccontrol.enums.PermissionEnum;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ParameterizedTest
@EnumSource(value= PermissionEnum.class, mode = EnumSource.Mode.EXCLUDE, names = "SYS_ADMIN")
public @interface PermissionWithoutSysAdminParameterizedTest {
}
