package person.winteryun.fileapi.aspect;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.RequestFacade;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class LogAspect {

    private static final String START_TIME = "startTime";

    private static final String REQUEST_PARAMS = "reqParam";

    private static final String REQUEST_IP = "reqIP";

    private static final String REQUEST_METHOD = "method";

    ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    @Pointcut("execution(public * person.winteryun.fileapi.controller.*.*(..))")
    public void log() {
    }

    @Before("log()")
    public void before(JoinPoint joinPoint) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> threadInfo = new HashMap<>();
        threadInfo.put(START_TIME, startTime);
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //请求的IP
        String ip = request.getRemoteAddr();
        //请求的参数
        String methodName = (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()");
        // 请求参数。
//        StringBuilder requestStr = new StringBuilder();
//        Object[] args = joinPoint.getArgs();
//        if (args != null && args.length > 0) {
//            for (Object arg : args) {
//                if (arg.getClass() != RequestFacade.class) {
//                    requestStr.append(arg.toString()).append(";");
//                }
//            }
//        }
        Enumeration<String> enumeration = request.getParameterNames();
        Map<String, String> parameterMap = new HashMap<>();
        while (enumeration.hasMoreElements()) {
            String parameter = enumeration.nextElement();
            parameterMap.put(parameter, request.getParameter(parameter));
        }
        String str = JSON.toJSONString(parameterMap);

        threadInfo.put(REQUEST_PARAMS, str);
        threadInfo.put(REQUEST_IP, ip);
        threadInfo.put(REQUEST_METHOD, methodName);
        threadLocal.set(threadInfo);
//        log.info("接口开始调用:requestData={}", threadInfo.get(REQUEST_PARAMS));
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
        String method = (String) threadInfo.get(REQUEST_METHOD);
        String ip = (String) threadInfo.get(REQUEST_IP);

        threadLocal.remove();
        log.info("{}接口调用:耗时={}ms,请求ip={},req={},result={}",
                method, takeTime, ip, req, res);
    }
}
