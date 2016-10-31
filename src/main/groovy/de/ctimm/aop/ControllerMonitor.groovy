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
    public Object logServiceAccess(ProceedingJoinPoint pjp) {
        def start
        try {
            start = System.currentTimeMillis()
            return pjp.proceed()
        } finally {
            def stop = System.currentTimeMillis()
            logger.info("Execution of {} took {} milliseconds",
                    pjp.getTarget().getClass().getName()
                            + "." + ((MethodSignature) pjp.getSignature()).getMethod().getName()
                            + "." + pjp.getArgs(),
                    stop - start)
        }
    }
}
