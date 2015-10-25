package demo.tuning.util.profiler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProfilerAspect {
    @Around("within(demo.tuning.controller..*)")
    public Object startProfile(ProceedingJoinPoint pjp) throws Throwable {
        ThreadProfiler.start(pjp.getSignature().toShortString());
        try {
            return pjp.proceed();
        } finally {
            ThreadProfiler.release();
            ThreadProfiler.logIfSlow(10);
        }
    }

    @Around("within(demo.tuning.biz..*) || within(demo.tuning.dao..*)")
    public Object doProfile(ProceedingJoinPoint pjp) throws Throwable {
        ThreadProfiler.enter(pjp.getSignature().toShortString());
        try {
            return pjp.proceed();
        } finally {
            ThreadProfiler.release();
        }
    }
}
