package br.com.kbmg.wsmusiccontrol.config.logging;

import br.com.kbmg.wsmusiccontrol.config.security.SpringSecurityUtil;
import br.com.kbmg.wsmusiccontrol.config.security.UserCredentialsSecurity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.LinkedHashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LoggingAdvice {

    @Autowired
    private ObjectMapper map;

    @Value("${app.logs}")
    private boolean appLogsEnabled;

    @Pointcut("within(br.com.kbmg.wsmusiccontrol.config.security.UserSpringSecurityService+)")
    public void pointcutLoadUserAndPermissions() {

    }

    @Pointcut("within(br.com.kbmg.wsmusiccontrol.service..*)")
    public void pointcutService() {

    }

    @Pointcut("within(br.com.kbmg.wsmusiccontrol.controller..*)")
    public void pointcutController() {

    }

    @Around("pointcutService() || pointcutController()")
    public Object auditMethod(ProceedingJoinPoint jp) throws Throwable {
        StopWatch watch = new StopWatch();
        generateLogObject(jp, MethodInvocationTypeEnum.METHOD_IN, null);

        watch.start();
        Object result = jp.proceed();
        watch.stop();

        generateLogObject(jp, MethodInvocationTypeEnum.METHOD_OUT, watch);
        return result;
    }

    @Around("pointcutLoadUserAndPermissions()")
    public Object auditUserAndRoles(ProceedingJoinPoint jp) throws Throwable {
        String methodName = jp.getSignature().getName();
        Object result = jp.proceed();

        if (methodName.equals("loadSpringSecurityInContext")) {
            log.warn(map.writeValueAsString(result));
        }

        return result;
    }

    private void generateLogObject(ProceedingJoinPoint jp, MethodInvocationTypeEnum methodInvocationTypeEnum, StopWatch watch) throws JsonProcessingException {

        if (appLogsEnabled) {
            String methodName = jp.getSignature().getName();
            String className = jp.getTarget().getClass().toString();
            UserCredentialsSecurity credentials = SpringSecurityUtil.getCredentials();

            if (credentials != null) {
                Object[] args = jp.getArgs();
                Map<Integer, String> argsMap = new LinkedHashMap<>();

                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    argsMap.put(i, arg != null ? arg.toString() : null);
                }

                LogObject logObject = new LogObject(credentials, className, methodName, argsMap, methodInvocationTypeEnum);
                String stringLogObject = map.writeValueAsString(logObject);

                String logInfo = MethodInvocationTypeEnum.METHOD_OUT.equals(methodInvocationTypeEnum) ?
                        String.format("%s - Execution time: %d ms", stringLogObject, watch.getTotalTimeMillis()) :
                        stringLogObject;

                log.info(logInfo);
            }
        }
    }

}
