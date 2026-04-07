package com.guanshiyun.mylong;

import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.extern.slf4j.Slf4j;
import reactor.util.context.ContextView;


@Slf4j
public class MyLong {

    public Long myLong(Object number) {
        Long mylong = null;;
        try {
            mylong = Long.parseLong(number.toString().trim());
        } catch (Exception e) {
            log.error("转换Long异常，number：{}", number);
            throw new RuntimeException("转换Long异常", e);
        }
        return mylong;
    }
    //重载，允许返回null
    public Long longOrNull(Object number) {
        Long mylong = null;;
        try {
            mylong = Long.parseLong(number.toString().trim());
        } catch (Exception e) {
            log.error("转换Long异常", e);
        }
        return mylong;
    }
    public Long findUserId(ContextView contextView) {
        return  myLong(contextView.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
    }

    public Long findTenantId(ContextView contextView) {
        return  myLong(contextView.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_TENANT_ID_KEY));
    }

    public Boolean hasKey(ContextView contextView) {
        return contextView.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
    }
}
