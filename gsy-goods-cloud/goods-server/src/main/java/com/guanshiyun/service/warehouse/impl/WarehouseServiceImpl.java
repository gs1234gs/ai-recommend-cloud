package com.guanshiyun.service.warehouse.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.ReactivePageQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.guanshiyun.controller.warehouse.vo.WarehouseSaveVO;
import com.guanshiyun.controller.warehouse.vo.WarehouseVO;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.repository.warehouse.WarehouseRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.warehouse.WarehouseService;
import com.guanshiyun.utils.BeanConvertUtil;
import com.guanshiyun.warehouse.Warehouse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final MyLong myLong;
    private final DatabaseClient databaseClient;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    //保存仓库信息,仓库ID为空则保存，非空则更新
    @Override
    public Mono<Long> save(WarehouseSaveVO warehouseSaveVO) {
        Warehouse warehouse = BeanUtil.toBean(warehouseSaveVO, Warehouse.class);
        return Mono.deferContextual(ctx -> {
                    if (!myLong.hasKey(ctx))
                        return Mono.error(new Throwable("用户未登录"));
                    Long useId = myLong.findUserId(ctx);
                    Long tenantId = myLong.findTenantId(ctx);
                    warehouse.setTenantId(tenantId);
                    LocalDateTime now = LocalDateTime.now();
                    if (Objects.isNull(warehouse.getId())) {
                        warehouse.setCreator(useId)
                                .setCreateTime(now);
                        return warehouseRepository.save(warehouse)
                                .map(Warehouse::getId);
                    }
                    warehouse.setUpdater(useId)
                            .setUpdateTime(now);
                    return r2dbcUpdateHelper.updateIgnoreNull(
                                    Warehouse.class,
                                    warehouse,
                                    Warehouse.Fields.id
                            )
                            .onErrorResume(throwable -> {
                                log.error("更新仓库信息失败", throwable);
                                return Mono.error(new Exception("更新仓库信息失败"));
                            });
                })
                .onErrorResume(throwable -> {
                    log.error("保存仓库信息失败", throwable);
                    return Mono.error(new Exception("保存仓库信息失败"));
                });

    }

    @Override
    public Mono<Long> deleteById(Long id) {
        return databaseClient.sql("delete from warehouse where id = :id")
                .bind(Warehouse.Fields.id, id)
                .fetch()
                .rowsUpdated()
                .onErrorResume(throwable -> {
                    log.error("删除仓库信息失败", throwable);
                    return Mono.error(new Exception("删除仓库信息失败"));
                });
    }

    @Override
    public Mono<Long> deleteAllById(Collection<Long> ids) {
        return databaseClient.sql("delete from warehouse where id in (:id)")
                .bind(Warehouse.Fields.id, ids)
                .fetch().rowsUpdated()
                .onErrorResume(throwable -> {
                    log.error("批量删除仓库信息失败", throwable);
                    return Mono.error(new Exception("批量删除仓库信息失败"));
                });
    }

    //分页查询仓库信息
    @Override
    public Mono<PageResultT<List<WarehouseVO>>> findPage(RequestPage<WarehouseVO> requestPage) {
        //校验参数
        RequestPage<WarehouseVO> chatRecordRequestPage = PageUtils.pageValidation(requestPage, WarehouseVO.class);
        //起始页码
        Long pageNum = chatRecordRequestPage.getPageNum();
        //每页数量
        Integer pageSize = PageUtils.pageSize(chatRecordRequestPage.getPageSize());
        Warehouse warehouse = BeanUtil.toBean(requestPage.getCondition(), Warehouse.class);
        RequestPage<Warehouse> page = RequestPage.<Warehouse>builder()
                .condition(warehouse)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
        return Mono.deferContextual(ctx -> {
                    if (!myLong.hasKey(ctx))
                        return Mono.error(new Throwable("用户未登录"));
                    Long tenantId = myLong.findTenantId(ctx);
                    return ReactivePageQuery.of(r2dbcEntityTemplate,
                                    Warehouse.class, page)
                            .like(Warehouse.Fields.name, requestPage.getCondition().getName())
                            .eq(Warehouse.Fields.tenantId, tenantId)
                            .page()
                            .map(pageResultT -> {
                                List<Warehouse> rows = pageResultT.getRows();
                                return PageResultT.<List<WarehouseVO>>builder()
                                        .pageNum(pageResultT.getPageNum())
                                        .pageSize(pageResultT.getPageSize())
                                        .total(pageResultT.getTotal())
                                        .rows(rows.stream()
                                                .map(warehouseItem ->
                                                        BeanUtil.toBean(warehouseItem, WarehouseVO.class)
                                                )
                                                .toList()
                                        )
                                        .build();
                            });
                })
                .onErrorResume(throwable -> {
                    log.error("分页查询仓库信息失败", throwable);
                    return Mono.error(new Exception("分页查询仓库信息失败"));
                });
    }

    @Override
    public Mono<Long> saveAll(List<Warehouse> warehouseList) {
        return warehouseRepository.saveAll(warehouseList)
                .count();
    }

    @Override
    public Mono<WarehouseVO> findById(Long id) {
        return warehouseRepository.findById(id)
                .map(warehouse -> BeanUtil.toBean(warehouse, WarehouseVO.class));
    }

    @Override
    public Mono<List<WarehouseVO>> findAll() {
        return warehouseRepository.findAll()
                .mapNotNull(item -> BeanConvertUtil.toBean(item, WarehouseVO.class))
                .collectList();
    }
}
