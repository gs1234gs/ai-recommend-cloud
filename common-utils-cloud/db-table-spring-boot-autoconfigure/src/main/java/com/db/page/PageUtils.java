package com.guanshiyun.utils;

import com.guanshiyun.consts.ConstNumber;
import com.guanshiyun.requestpojo.RequestPage;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Objects;

/**
 * 分页条件设置默认值
 */
@Slf4j
public class PageUtils {
    //判断requestPage是否为空
    public static <T> RequestPage<T> pageValidation(RequestPage<T> requestPage, Class<T> clazz) {
        T condition = null;
        try {
            condition = clazz.getDeclaredConstructor().newInstance(); // 调用无参构造函数
        } catch (Exception e) {
            throw new RuntimeException("无法创建 " + clazz.getSimpleName() + " 实例", e);
        }
        //判断requestPage是否为空
        return Objects.isNull(requestPage) ?
                //如果为空，则返回一个默认的RequestPage对象
                RequestPage.<T>builder()
                        .pageNum(BigInteger.ZERO)
                        .pageSize(ConstNumber.INT_ZERO)
                        .condition(condition)
                        .build()
                :
                //如果不为空，则判断condition是否为空,pageNum是否为空,pageSize是否为空
                (Objects.isNull(requestPage.getCondition())
                        ?
                        //如果condition为空，则设置condition为condition
                        requestPage.setCondition(condition)
                                .setPageNum(
                                        pageNum(requestPage.getPageNum())
                                )
                                .setPageSize(
                                        pageSize(requestPage.getPageSize())
                                )
                        :
                        //如果不为空，则返回requestPage
                        requestPage.setPageNum(
                                pageNum(requestPage.getPageNum())
                                )
                                .setPageSize(
                                        pageSize(requestPage.getPageSize())
                                )
                );
    }

    //判断pageNum和pageSize是否合法
    //获取pageNum
    public static BigInteger pageNum(BigInteger pageNum) {
        try {
            return (
                    pageNum != null
                            &&
                            pageNum.compareTo(BigInteger.ZERO)
                                    >
                                    ConstNumber.INT_ZERO
            )
                    ?
                    pageNum : BigInteger.ONE;
        } catch (Exception e) {
            log.error("获取pageNum失败", e);
        }
        return BigInteger.ONE;
    }

    //获取pageSize
    public static Integer pageSize(Integer pageSize) {
        try {
            return (
                    pageSize != null
                            &&
                            pageSize > ConstNumber.INTEGER_ZERO
            )
                    ?
                    pageSize : ConstNumber.INT_TEN;
        } catch (Exception e) {
            log.error("获取pageSize失败", e);
        }

        return ConstNumber.INT_TEN;
    }
}
