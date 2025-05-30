package com.shousi.login;

import com.shousi.constant.RedisConstant;
import com.shousi.entity.UserInfo;
import com.shousi.execption.GuiguException;
import com.shousi.result.ResultCodeEnum;
import com.shousi.util.AuthContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

@Slf4j
@Component
@Aspect
public class LoginAspect {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Around("@annotation(com.shousi.login.TingShuLogin)")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("进入切面");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 从请求头中拿到 token 值
        String token = request.getHeader("token");
        // 获取到执行方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        // 拿到方法注解的字段属性值进行判断
        TingShuLogin annotation = method.getAnnotation(TingShuLogin.class);
        if (annotation.required()) {
            // 需要登陆
            // 没有token值
            if (!StringUtils.hasText(token)) {
                throw new GuiguException(ResultCodeEnum.UN_LOGIN);
            }
            // 如果token值不为空，但是信息过期
            UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
            if (userInfo == null) {
                throw new GuiguException(ResultCodeEnum.UN_LOGIN);
            }
        }
        if (StringUtils.hasText(token)) {
            UserInfo userInfo = (UserInfo) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
            if (userInfo != null) {
                AuthContextHolder.setUserId(userInfo.getId());
                AuthContextHolder.setUsername(userInfo.getNickname());
            }
        }
        // 登录完成后，执行方法体中的逻辑代码
        return joinPoint.proceed();
    }
}
