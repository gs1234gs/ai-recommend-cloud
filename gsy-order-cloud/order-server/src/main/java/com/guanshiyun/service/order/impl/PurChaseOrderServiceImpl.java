package com.guanshiyun.service.order.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.ReactivePageQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.controller.address.vo.OrderAddressVO;
import com.guanshiyun.controller.order.vo.PurChaseOrderSaveVO;
import com.guanshiyun.controller.order.vo.PurChaseOrderVO;
import com.guanshiyun.order.PurChaseOrder;
import com.guanshiyun.repository.address.OrderAddressRepository;
import com.guanshiyun.repository.order.PurChaseOrderRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.order.PurChaseOrderService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurChaseOrderServiceImpl implements PurChaseOrderService {
    private final PurChaseOrderRepository purChaseOrderRepository;
    private final MyBigInteger myBigInteger;
    private final SnowflakePermanent snowflakePermanent;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TransactionalOperator transactionalOperator;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final OrderAddressRepository orderAddressRepository;

    /**
     * 保存订单
     */
    @Override
    public Mono<BigInteger> save(PurChaseOrderSaveVO purChaseOrderSaveVO) {
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)){
                return Mono.error(new RuntimeException("用户未登录"));
            }

            BigInteger userId =
                    myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            BigInteger id = snowflakePermanent.nextId();
            //订单
            PurChaseOrder purChaseOrder =
                    BeanUtil.toBean(purChaseOrderSaveVO, PurChaseOrder.class);
            purChaseOrder
                    .setId(id)
                    .setCreator(userId)
                    .setCreateTime(LocalDateTime.now());
            return r2dbcEntityTemplate.insert(purChaseOrder)
                    .map(PurChaseOrder::getId)
                    .transform(transactionalOperator::transactional);
        });
    }

    /**
     * 修改订单
     */
    @Override
    public Mono<BigInteger> updateById(PurChaseOrderSaveVO purChaseOrderSaveVO) {
        Integer status = purChaseOrderSaveVO.getStatus();
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            BigInteger userId = myBigInteger.bigInteger(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
            );
            return r2dbcUpdateHelper.updateIgnoreNull(
                            EntityTableNameUtils.getName(PurChaseOrder.class),
                            PurChaseOrder.builder()
                                    .id(purChaseOrderSaveVO.getId())
                                    .updater(userId)
                                    .updateTime(LocalDateTime.now())
                                    .status(status)
                                    .addressId(purChaseOrderSaveVO.getAddressId())
                                    .build(),
                            PurChaseOrder.Fields.id)
                    .transform(transactionalOperator::transactional)
                    .onErrorResume(throwable -> {
                        log.error("修改订单失败：", throwable);
                        return Mono.error(new Exception("修改订单失败"));
                    });
        });
    }

    @Override
    public Mono<List<PurChaseOrderVO>> findByUserId(BigInteger userId, Integer rows) {
        return purChaseOrderRepository.findAllByUserId(userId, rows)
                .map(purChaseOrder -> BeanUtil.toBean(purChaseOrder, PurChaseOrderVO.class))
                .collectList();
    }

    @Override
    public Mono<List<PurChaseOrderVO>> findByUserIds(List<BigInteger> userIds, Integer rows) {
        return purChaseOrderRepository.findAllByUserIds(userIds, rows)
                .map(purChaseOrder ->
                        BeanUtil.toBean(purChaseOrder, PurChaseOrderVO.class)
                )
                .collectList();
    }

    @Override
    public Mono<PurChaseOrderVO> findById(BigInteger id) {
        return purChaseOrderRepository.findById(id)
                .map(purChaseOrder ->
                        BeanUtil.toBean(purChaseOrder, PurChaseOrderVO.class)
                );
    }

    @Override
    public Mono<PageResultT<List<PurChaseOrderVO>>> findByPage(RequestPage<PurChaseOrderVO> requestPage) {
        RequestPage<PurChaseOrderVO> purChaseOrderVORequestPage = PageUtils.pageValidation(requestPage, PurChaseOrderVO.class);
        RequestPage<PurChaseOrder> orderRequestPage = BeanConvertUtil.toBean(purChaseOrderVORequestPage, PurChaseOrder.class);
        return ReactivePageQuery.of(r2dbcEntityTemplate, PurChaseOrder.class, orderRequestPage)
                .page()
                .map(pageResultT ->
                        PageResultT.<List<PurChaseOrderVO>>builder()
                                .pageNum(pageResultT.getPageNum())
                                .pageSize(pageResultT.getPageSize())
                                .total(pageResultT.getTotal())
                                .rows(BeanConvertUtil.toBeanList(pageResultT.getRows(), PurChaseOrderVO.class))
                                .build()
                );
    }

    @Override
    public Mono<PageResultT<List<PurChaseOrderVO>>> findByUserIdPage(RequestPage<PurChaseOrderVO> requestPage) {
        RequestPage<PurChaseOrderVO> purChaseOrderVORequestPage = PageUtils.pageValidation(requestPage, PurChaseOrderVO.class);
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            BigInteger userId = myBigInteger.bigInteger(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
            );
            RequestPage<PurChaseOrder> orderRequestPage =
                    BeanConvertUtil.toBean(purChaseOrderVORequestPage,
                            PurChaseOrder.class);
            orderRequestPage.getCondition().setCreator(userId);
            return ReactivePageQuery.of(r2dbcEntityTemplate, PurChaseOrder.class, orderRequestPage)
                    .page()
                    .map(pageResultT ->
                            PageResultT.<List<PurChaseOrderVO>>builder()
                                    .pageNum(pageResultT.getPageNum())
                                    .pageSize(pageResultT.getPageSize())
                                    .total(pageResultT.getTotal())
                                    .rows(BeanConvertUtil.toBeanList(pageResultT.getRows(), PurChaseOrderVO.class))
                                    .build()
                    );
        });
    }

    @Override
    public Mono<List<PurChaseOrderVO>> findByRows(Integer rows) {
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            BigInteger userId = myBigInteger.bigInteger(
                    ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)
            );
            return purChaseOrderRepository.findAllByUserId(userId, rows)
                    .collectList()
                    .flatMap(purChaseOrderVOS ->
                            orderAddressRepository.findAllById(purChaseOrderVOS
                                            .stream()
                                            .map(PurChaseOrder::getAddressId)
                                            .toList())
                                    .collectList()
                                    .map(orderAddress -> {
                                        Map<BigInteger, List<PurChaseOrder>> orderGroupByAddressId =
                                                purChaseOrderVOS
                                                        .stream()
                                                        .collect(Collectors.groupingBy(PurChaseOrder::getAddressId));
                                        Map<BigInteger, OrderAddressVO> addressGroupById =
                                                BeanConvertUtil.toBeanList(orderAddress, OrderAddressVO.class)
                                                        .stream()
                                                        .collect(Collectors
                                                                .toMap((OrderAddressVO::getId),Function.identity()));
                                       return orderAddress.stream()
                                                .flatMap(item ->
                                                    orderGroupByAddressId.getOrDefault(item.getId(), List.of())
                                                            .stream()
                                                            .map(purChaseOrder ->
                                                                    BeanConvertUtil.toBean(purChaseOrder, PurChaseOrderVO.class)
                                                                            .setOrderAddressVO(addressGroupById.getOrDefault(item.getId(),
                                                                                    OrderAddressVO.builder().build()))
                                                            )
                                                )
                                                .toList();
                                    })

                    );

        });
    }
}
