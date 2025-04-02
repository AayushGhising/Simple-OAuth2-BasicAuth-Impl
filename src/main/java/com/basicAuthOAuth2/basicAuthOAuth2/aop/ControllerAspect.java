package com.basicAuthOAuth2.basicAuthOAuth2.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class ControllerAspect {

    private static final Logger log = LoggerFactory.getLogger(ControllerAspect.class);

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    public void controllerPointcut() {
        // Method is empty as this is just a Pointcut definition
    }

    @Around("controllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Request initiated: {}.{}() with arguments: {}",
                className, methodName, Arrays.toString(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            long elapsedTime = System.currentTimeMillis() - start;

            log.info("Request completed: {}.{}() with result: {} in {}ms",
                    className, methodName, result, elapsedTime);

            return result;
        } catch (Exception e) {
            log.error("Exception in {}.{}() with cause: {}",
                    className, methodName, e.getMessage());
            throw e;
        }
    }

    @AfterThrowing(pointcut = "controllerPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.error("Exception in {}.{}() with cause: {} and message: {}",
                className, methodName, e.getCause() != null ? e.getCause() : "NULL", e.getMessage());
    }
}