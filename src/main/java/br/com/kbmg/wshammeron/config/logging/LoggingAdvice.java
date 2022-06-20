package br.com.kbmg.wshammeron.config.logging;

import br.com.kbmg.wshammeron.config.security.SpringSecurityUtil;
import br.com.kbmg.wshammeron.config.security.UserCredentialsSecurity;
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

    @Autowired
    public LogService logService;

    @Pointcut("within(br.com.kbmg.wshammeron.service..*)")
    public void pointcutService() {

    }

    @Pointcut("within(br.com.kbmg.wshammeron.controller..*)")
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

    private void generateLogObject(ProceedingJoinPoint jp, MethodInvocationTypeEnum methodInvocationTypeEnum, StopWatch watch) {

        if (appLogsEnabled) {
            String methodName = jp.getSignature().getName();
            String className = jp.getTarget().getClass().toString();
            UserCredentialsSecurity credentials = SpringSecurityUtil.getCredentials();

            Object[] args = jp.getArgs();
            Map<Integer, String> argsMap = new LinkedHashMap<>();

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                argsMap.put(i, arg != null ? arg.toString() : null);
            }

            LogTraceApp logTraceApp = new LogTraceApp(
                    credentials,
                    className,
                    methodName,
                    argsMap,
                    methodInvocationTypeEnum,
                    MethodInvocationTypeEnum.METHOD_OUT.equals(methodInvocationTypeEnum) ?
                            watch.getTotalTimeMillis() : null
            );

            logService.logTraceApp(logTraceApp);
        }
    }

}
