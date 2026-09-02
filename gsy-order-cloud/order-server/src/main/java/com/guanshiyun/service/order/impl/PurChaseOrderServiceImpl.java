package com.guanshiyun.service.order.impl;

import com.db.cursorQuery.ReactivePageQuery;
import com.db.cursorQuery.ReactiveQuery;
import com.db.dbnumber.ConstNumber;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.base.BasePojo;
import com.guanshiyun.controller.order.vo.PurChaseOrderSaveVO;
import com.guanshiyun.controller.order.vo.PurChaseOrderVO;
import com.guanshiyun.controller.order.vo.PurchaseOrderDetailVO;
import com.guanshiyun.controller.order.vo.PurchaseOrderSearchVO;
import com.guanshiyun.feedback.Feedback;
import com.guanshiyun.gorseenum.GorseFeedbackEnum;
import com.guanshiyun.goser.GorseClient;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.order.PurChaseOrder;
import com.guanshiyun.profile.ProductApiVO;
import com.guanshiyun.profile.SKUApiVO;
import com.guanshiyun.profile.TagApiVO;
import com.guanshiyun.repository.address.OrderAddressRepository;
import com.guanshiyun.repository.order.PurChaseOrderRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.rpc.address.vo.OrderAddressVOApi;
import com.guanshiyun.rpc.goodsapi.product.ProductApiService;
import com.guanshiyun.rpc.goodsapi.sku.SkuApiService;
import com.guanshiyun.rpc.goodsapi.tag.TagApiService;
import com.guanshiyun.rpc.order.vo.PurchaseOrderVOApi;
import com.guanshiyun.service.order.PurChaseOrderService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurChaseOrderServiceImpl implements PurChaseOrderService {
    private final PurChaseOrderRepository purChaseOrderRepository;
    private final MyLong myLong;
    private final SnowflakePermanent snowflakePermanent;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final TransactionalOperator transactionalOperator;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final OrderAddressRepository orderAddressRepository;
    private final SkuApiService skuApiService;
    private final TagApiService tagApiService;
    private final ProductApiService productApiService;
    private final GorseClient gorseClient;
    private final ReactiveQuery reactiveQuery;


    /**
     * 保存订单,同时调用远程接口扣减库存
     */
    @Override
    public Mono<Long> save(PurChaseOrderSaveVO purChaseOrderSaveVO) {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            Long userId = myLong.findUserId(ctx);
            Long id = snowflakePermanent.nextId();
            LocalDateTime now = LocalDateTime.now();
            //订单
            PurChaseOrder purChaseOrder =
                    BeanConvertUtil.toBean(purChaseOrderSaveVO, PurChaseOrder.class);
            purChaseOrder
                    .setId(id)
                    .setOrderPlacementTime(now)
                    .setPayTime(now)
                    .setOrderNo(snowflakePermanent.stringNextId())
                    .setCreator(userId)
                    .setCreateTime(now);
            return skuApiService.findTenantIdBySkuId(purChaseOrder.getSkuId())
                    .flatMap(tenantIdR -> {
                        Long tenantId = tenantIdR.getData();
                        purChaseOrder.setTenantId(tenantId);
                        //保存订单
                        return r2dbcEntityTemplate.insert(purChaseOrder)
                                .flatMap(order ->
                                        skuApiService.reduceStockAndAddSales(order.getSkuId(), purChaseOrderSaveVO.getNum())
                                                .thenReturn(order.getId())
                                )
                                .publishOn(Schedulers.boundedElastic())
                                .doOnSuccess(ok -> {
                                    Long productId = purChaseOrder.getProductId();
                                    //同步购买行为到 Gorse
                                    gorseClient.insertFeedback(
                                                    List.of(
                                                            Feedback.builder()
                                                                    .itemId(productId.toString())
                                                                    .feedbackType(GorseFeedbackEnum.PURCHASE.getValue())
                                                                    .userId(userId.toString())
                                                                    .timestamp(now.format(DateTimeFormatter.ISO_DATE_TIME))
                                                                    .build()
                                                    )
                                            )
                                            .onErrorResume(e -> {
                                                log.error("同步购买行为到 Gorse 失败，订单 ID: {}", productId, e);
                                                return Mono.error(new Throwable(e)); //
                                            })
                                            .subscribe();
                                });
                    });
        });
    }

    /**
     * 修改订单
     */
    @Override
    public Mono<Long> updateById(PurChaseOrderSaveVO purChaseOrderSaveVO) {
        Integer status = purChaseOrderSaveVO.getStatus();
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            Long userId = myLong.findUserId(ctx);
            return r2dbcUpdateHelper.updateIgnoreNull(
                            PurChaseOrder.class,
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
    public Mono<List<PurChaseOrderVO>> findByUserId(Long userId, Integer rows) {
        return purChaseOrderRepository.findAllByUserId(userId, rows)
                .map(purChaseOrder -> BeanConvertUtil.toBean(purChaseOrder, PurChaseOrderVO.class))
                .collectList();
    }

    @Override
    public Mono<List<PurChaseOrderVO>> findByUserIds(List<Long> userIds, Integer rows) {
        return purChaseOrderRepository.findAllByUserIds(userIds, rows)
                .map(purChaseOrder ->
                        BeanConvertUtil.toBean(purChaseOrder, PurChaseOrderVO.class)
                )
                .collectList();
    }

    @Override
    public Mono<PurchaseOrderDetailVO> findById(Long id) {
        return purChaseOrderRepository.findById(id)
                .flatMap(purChaseOrder -> {
                    Long productId = purChaseOrder.getProductId();
                    Long skuId = purChaseOrder.getSkuId();
                    return Mono.zip(skuApiService.findBySkuId(skuId),
                                    tagApiService.findByProductId(productId),
                                    productApiService.findProductById(productId)
                            )
                            .map(tuple -> {
                                SKUApiVO skuApiVO = tuple.getT1().getData();
                                List<TagApiVO> tagApiVO = tuple.getT2().getData();
                                ProductApiVO productApiVO = tuple.getT3().getData();
                                return BeanConvertUtil.toBean(purChaseOrder, PurchaseOrderDetailVO.class)
                                        .setName(productApiVO.getName())
                                        .setSku(skuApiVO)
                                        .setTag(tagApiVO);
                            })
                            .doOnError(throwable -> {
                                log.error("查询异常", throwable);
                            });

                });
    }

    @Override
    public Mono<PageResultT<List<PurChaseOrderVO>>> findByPage(RequestPage<PurchaseOrderSearchVO> requestPage) {
        RequestPage<PurchaseOrderSearchVO> purChaseOrderVORequestPage = PageUtils.pageValidation(requestPage, PurchaseOrderSearchVO.class);
        RequestPage<PurChaseOrder> orderRequestPage = BeanConvertUtil.toBean(purChaseOrderVORequestPage, PurChaseOrder.class);
        PurchaseOrderSearchVO condition = purChaseOrderVORequestPage.getCondition();
        return reactiveQuery.createQuery(PurChaseOrder.class, orderRequestPage)
                .gte(BasePojo.Fields.createTime, condition.getStartTime())
                .lte(BasePojo.Fields.createTime, condition.getEndTime())
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
    public Mono<PageResultT<List<PurChaseOrderVO>>> findByUserIdPage(RequestPage<PurchaseOrderSearchVO> requestPage) {
        RequestPage<PurchaseOrderSearchVO> purChaseOrderVORequestPage = PageUtils.pageValidation(requestPage, PurchaseOrderSearchVO.class);
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            Long userId = myLong.findUserId(ctx);
            RequestPage<PurChaseOrder> orderRequestPage =
                    BeanConvertUtil.toBean(purChaseOrderVORequestPage,
                            PurChaseOrder.class);
            orderRequestPage.getCondition().setCreator(userId);
            return ReactivePageQuery.of(r2dbcEntityTemplate, PurChaseOrder.class, orderRequestPage)
                    .eq(BasePojo.Fields.creator, userId)
                    .page()
                    .flatMap(pageResultT -> {
                                List<PurChaseOrder> rows = pageResultT.getRows();
                                List<Long> skuIdList = rows.stream().map(PurChaseOrder::getSkuId).toList();
//                                List<Long> productIdList = rows.stream().map(PurChaseOrder::getProductId).toList();
                                if (skuIdList.isEmpty()) {
                                    return Mono.just(PageResultT.<List<PurChaseOrderVO>>builder()
                                            .pageNum(pageResultT.getPageNum())
                                            .pageSize(pageResultT.getPageSize())
                                            .total(pageResultT.getTotal())
                                            .rows(new ArrayList<>())
                                            .build());
                                }
                                return skuApiService.findBySkuIds(skuIdList)
                                        .map(skuList -> {
                                            Map<Long, SKUApiVO> skuMap = skuList.getData().stream()
                                                    .collect(Collectors.toMap(
                                                            SKUApiVO::getId,      // key = skuId
                                                            Function.identity()   // value = SKUApiVO
                                                    ));
                                            List<PurChaseOrderVO> purChaseOrderVOS = rows.stream()
                                                    .map(p -> {
                                                        // 安全获取 SKU 名称
                                                        String skuName = Optional.ofNullable(p.getSkuId())
                                                                .map(skuMap::get)           // 用 skuId 查 SKU
                                                                .map(SKUApiVO::getName)
                                                                .orElse("未知商品");
                                                        String skuImage = Optional.ofNullable(p.getSkuId())
                                                                .map(skuMap::get)                     // SKUApiVO
                                                                .map(SKUApiVO::getPicList)            // List<String>
                                                                .filter(list -> !list.isEmpty())      // 确保非空
                                                                .map(list -> list.getFirst())         // 取第一张
                                                                .orElse("https://default.com/placeholder.png"); // 默认图

                                                        return BeanConvertUtil.toBean(p, PurChaseOrderVO.class)
                                                                .setName(skuName)
                                                                .setImage(skuImage);
                                                    }).toList();
                                            return PageResultT.<List<PurChaseOrderVO>>builder()
                                                    .pageNum(pageResultT.getPageNum())
                                                    .pageSize(pageResultT.getPageSize())
                                                    .total(pageResultT.getTotal())
                                                    .rows(purChaseOrderVOS)
                                                    .build();
                                        });

                            }
                    );
        });
    }

    @Override
    public Mono<List<PurchaseOrderVOApi>> findByRows(Integer rows) {
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }

            Long userId = myLong.findUserId(ctx);

            return purChaseOrderRepository.findAllByUserId(userId, rows)
                    .collectList()
                    .flatMap(purChaseOrders -> {
                        // 空值检查
                        if (purChaseOrders == null || purChaseOrders.isEmpty()) {
                            return Mono.just(new ArrayList<PurchaseOrderVOApi>());
                        }

                        // 提取地址 ID，过滤 null 值
                        List<Long> addressIds = purChaseOrders.stream()
                                .map(PurChaseOrder::getAddressId)
                                .filter(Objects::nonNull)
                                .distinct()
                                .toList();

                        // 如果没有地址 ID，直接返回订单列表（不带地址信息）
                        if (addressIds.isEmpty()) {
                            return Mono.just(purChaseOrders.stream()
                                    .map(purChaseOrder -> BeanConvertUtil.toBean(purChaseOrder, PurchaseOrderVOApi.class)
                                            .setOrderAddressVO(OrderAddressVOApi.builder().build()))
                                    .toList());
                        }

                        return orderAddressRepository.findAllById(addressIds)
                                .collectList()
                                .map(orderAddresses -> {
                                    // 空值检查
                                    if (orderAddresses == null || orderAddresses.isEmpty()) {
                                        return purChaseOrders.stream()
                                                .map(purChaseOrder -> BeanConvertUtil.toBean(purChaseOrder, PurchaseOrderVOApi.class)
                                                        .setOrderAddressVO(OrderAddressVOApi.builder().build()))
                                                .toList();
                                    }

                                    // 构建地址 ID 到地址对象的映射
                                    Map<Long, OrderAddressVOApi> addressGroupById =
                                            BeanConvertUtil.toBeanList(orderAddresses, OrderAddressVOApi.class)
                                                    .stream()
                                                    .filter(Objects::nonNull)
                                                    .filter(addr -> addr.getId() != null)
                                                    .collect(Collectors.toMap(
                                                            OrderAddressVOApi::getId,
                                                            Function.identity(),
                                                            (v1, v2) -> v1
                                                    ));

                                    // 构建地址 ID 到订单列表的映射
                                    Map<Long, List<PurChaseOrder>> orderGroupByAddressId =
                                            purChaseOrders.stream()
                                                    .filter(Objects::nonNull)
                                                    .filter(o -> o.getAddressId() != null)
                                                    .collect(Collectors.groupingBy(
                                                            PurChaseOrder::getAddressId
                                                    ));

                                    // 融合订单和地址信息
                                    return purChaseOrders.stream()
                                            .filter(Objects::nonNull)
                                            .map(purChaseOrder -> {
                                                PurchaseOrderVOApi vo = BeanConvertUtil.toBean(purChaseOrder, PurchaseOrderVOApi.class);
                                                if (vo == null) {
                                                    return null;
                                                }

                                                Long addressId = purChaseOrder.getAddressId();
                                                OrderAddressVOApi addressVO = addressId != null
                                                        ? addressGroupById.get(addressId)
                                                        : null;

                                                vo.setOrderAddressVO(addressVO != null
                                                        ? addressVO
                                                        : OrderAddressVOApi.builder().build());

                                                return vo;
                                            })
                                            .filter(Objects::nonNull)
                                            .toList();
                                });
                    })
                    .onErrorResume(e -> {
                        log.error("查询用户订单失败", e);
                        return Mono.just(new ArrayList<>());
                    });
        });
    }

    @Override
    public Mono<Boolean> deleteById(Long id) {
        // 构建更新操作
        return Mono.deferContextual(ctx -> {
            if (!myLong.hasKey(ctx)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            Long userId = myLong.findUserId(ctx);
            return r2dbcEntityTemplate.update(PurChaseOrder.class)
                    .matching(Query.query(
                            Criteria.where(PurChaseOrder.Fields.id).is(id)
                    ))
                    .apply(
                            Update.update(BasePojo.Fields.delFlag, ConstNumber.INT_ONE)
                                    .set(BasePojo.Fields.updater, userId)
                                    .set(BasePojo.Fields.updateTime, LocalDateTime.now()))
                    .then() // 转换为 Mono<Void> 表示完成
                    .thenReturn(true) // 成功后返回 true
                    .onErrorResume(e -> {
                        log.error("删除失败", e);
                        return Mono.just(false);
                    });
        });
    }
}
