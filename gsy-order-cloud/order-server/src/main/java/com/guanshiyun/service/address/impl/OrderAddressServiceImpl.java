package com.guanshiyun.service.address.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.ReactivePageQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.address.OrderAddress;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.controller.address.vo.OrderAddressSaveVO;
import com.guanshiyun.controller.address.vo.OrderAddressVO;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.repository.address.OrderAddressRepository;
import com.guanshiyun.repository.order.PurChaseOrderRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.address.OrderAddressService;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAddressServiceImpl implements OrderAddressService {
    private final OrderAddressRepository orderAddressRepository;
    private final MyLong myLong;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final PurChaseOrderRepository purChaseOrderRepository;

    /**
     * 添加更新地址
     */
    @Override
    public Mono<Long> save(OrderAddressSaveVO orderAddressSaveVO) {
        OrderAddress orderAddress = BeanUtil.toBean(orderAddressSaveVO, OrderAddress.class);
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx))
                return Mono.error(new RuntimeException("用户未登录"));
            Long userId =
                    myLong.findUserId(ctx);

            if (Objects.isNull(orderAddress.getId())) {
                orderAddress.setCreator(userId);
                orderAddress.setCreateTime(LocalDateTime.now());
                return orderAddressRepository.save(orderAddress)
                        .map(OrderAddress::getId)
                        .switchIfEmpty(Mono.error(new RuntimeException("添加失败")));
            }
            orderAddress.setUpdater(userId);
            orderAddress.setUpdateTime(LocalDateTime.now());
            return r2dbcUpdateHelper.updateIgnoreNull(
                            EntityTableNameUtils.getName(OrderAddress.class),
                            orderAddress,
                            OrderAddress.Fields.id)
                    .onErrorResume(throwable -> {
                        log.error("更新地址失败：", throwable);
                        return Mono.error(new Exception("更新地址失败"));
                    });
        });
    }

    /**
     * 这里进行的是逻辑删除
     */
    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx))
                return Mono.error(new RuntimeException("用户未登录"));
            Long userId =
                    myLong.findUserId(ctx);
            String name = EntityTableNameUtils.getName(OrderAddress.class);;
            OrderAddress orderAddress = OrderAddress.builder()
                    .id(id)
                    .updater(userId)
                    .updateTime(LocalDateTime.now())
                    .delFlag((short) 1)
                    .build();
            return r2dbcUpdateHelper.updateIgnoreNull(
                            name,
                            orderAddress,
                            OrderAddress.Fields.id)
                    .then()
                    .onErrorResume(throwable -> {
                        log.error("删除地址失败：", throwable);
                        return Mono.error(new Exception("删除地址失败"));
                    });
        });
    }

    @Override
    public Mono<OrderAddressVO> findByOrderId(Object orderId) {
        return purChaseOrderRepository.findById(myLong.myLong(orderId))
                .flatMap(purChaseOrder -> orderAddressRepository.findById(purChaseOrder.getAddressId())
                        .map(orderAddress -> BeanUtil.toBean(orderAddress, OrderAddressVO.class)))
                .onErrorResume(throwable -> {
                    log.error("查询地址失败：", throwable);
                    return Mono.error(new Exception("查询地址失败"));
                });


    }

    @Override
    public Mono<List<OrderAddressVO>> findByUserId(Long userId) {
        return orderAddressRepository.findByUserId(userId)
                .map(orderAddress ->
                        BeanUtil.toBean(orderAddress, OrderAddressVO.class))
                .collectList()
                .onErrorResume(throwable -> {
                    log.error("查询地址失败：", throwable);
                    return Mono.error(new Exception("查询地址失败"));
                });
    }

    @Override
    public Mono<List<OrderAddressVO>> findByUserId() {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx))
                return Mono.error(new RuntimeException("用户未登录"));
            Long userId =
                    myLong.findUserId(ctx);

            return orderAddressRepository.findByUserId(userId)
                    .map(orderAddress ->
                            BeanUtil.toBean(orderAddress, OrderAddressVO.class))
                    .collectList()

                    .onErrorResume(throwable -> {
                        log.error("查询地址失败：", throwable);
                        return Mono.error(new Exception("查询地址失败"));
                    });
        });
    }

    @Override
    public Mono<List<OrderAddressVO>> findByOrderIds(Collection<Long> orderIds) {
        return purChaseOrderRepository.findAllAddressById(orderIds)
                .collectList()
                .flatMap(addressListId ->
                        orderAddressRepository.findByOrderIds(addressListId)
                                .map(orderAddress -> BeanUtil.toBean(orderAddress, OrderAddressVO.class))
                                .collectList()
                )
                .onErrorResume(throwable -> {
                    log.error("查询地址失败：", throwable);
                    return Mono.error(new Exception("查询地址失败"));
                });
    }

    @Override
    public Mono<OrderAddressVO> findById(Long id) {
        return orderAddressRepository.findById(id)
                .map(orderAddress -> BeanUtil.toBean(orderAddress, OrderAddressVO.class));
    }

    @Override
    public Mono<PageResultT<List<OrderAddressVO>>> findByPage(RequestPage<OrderAddressVO> requestPage) {
        RequestPage<OrderAddressVO> orderAddressVORequestPage = PageUtils.pageValidation(requestPage, OrderAddressVO.class);
        RequestPage<OrderAddress> page = BeanConvertUtil.toBean(orderAddressVORequestPage, OrderAddress.class);

        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx))
                return Mono.error(new RuntimeException("用户未登录"));
            Long userId = myLong.findUserId(ctx);
           return ReactivePageQuery.of(r2dbcEntityTemplate, OrderAddress.class, page)
                   .eq(BasePojo.Fields.creator, userId)
                    .page()
                    .map(pageResultT -> PageResultT.<List<OrderAddressVO>>builder()
                            .pageNum(pageResultT.getPageNum())
                            .pageSize(pageResultT.getPageSize())
                            .total(pageResultT.getTotal())
                            .rows(BeanConvertUtil.toBeanList(pageResultT.getRows(), OrderAddressVO.class))
                            .build()

                    );
        });
    }
}
