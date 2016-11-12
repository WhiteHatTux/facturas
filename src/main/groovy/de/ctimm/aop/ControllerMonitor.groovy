package de.ctimm.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * @author Christopher Timm <christopher.timm@endicon.de>
 *
 */
@Aspect
@Component
class ControllerMonitor {
    private static final Logger logger = LoggerFactory.getLogger(ControllerMonitor.class)

    @Around("execution(* de..*Controller.*(..))")
    public static Object logServiceAccess(ProceedingJoinPoint pjp) {
        def start = System.currentTimeMillis()
        try {
            return pjp.proceed()
        } finally {
            def stop = System.currentTimeMillis()
            StringBuilder s = new StringBuilder()
                    .append(pjp.getTarget().getClass().getName())
                    .append(".")
                    .append(((MethodSignature) pjp.getSignature()).getMethod().getName())
                    .append("(")
            pjp.getArgs().each {
                if (pjp.getArgs().last().is(it)) {
                    s.append(it)
                } else {
                    s.append(it).append(",")
                }
            }
            s.append(")")
            logger.info("Execution of {} took {} milliseconds",
                    s,
                    stop - start)
        }
    }
}
