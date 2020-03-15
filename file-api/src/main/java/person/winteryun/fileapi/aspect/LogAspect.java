package person.winteryun.fileapi.aspect;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LogAspect {

    private static final String START_TIME = "startTime";

    private static final String REQUEST_PARAMS = "reqParam";

    ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @Pointcut("execution(public * person.winteryun.fileapi.controller.*.*(..))")
    public void log() {
    }

    @Before("log()")
    public void before(JoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put(START_TIME, startTime);
        // 请求参数。
        StringBuilder requestStr = new StringBuilder();
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                requestStr.append(arg.toString());
            }
        }
        threadInfo.put(REQUEST_PARAMS, requestStr.toString());
        threadLocal.set(threadInfo);
        log.info("接口开始调用:requestData={}", threadInfo.get(REQUEST_PARAMS));
    }


    @After("log()")
    public void after(JoinPoint joinPoint) {

    }

    @AfterReturning(value = "log()", returning = "obj")
    public void doAfterReturn(Object obj) {
        Map<String, Object> threadInfo = threadLocal.get();
        long takeTime = System.currentTimeMillis() - (long) threadInfo.getOrDefault(START_TIME, System.currentTimeMillis());
        String req = JSON.toJSONString(threadInfo.get(REQUEST_PARAMS));
        String res = JSON.toJSONString(obj);
        threadLocal.remove();
        log.info("接口调用:耗时={}ms,req={},result={}",
                takeTime,req, res);
    }
}
