package com.guanshiyun.service.warehouse.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.ReactivePageQuery;
import com.db.page.PageUtils;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.controller.warehouse.vo.WarehouseSaveVO;
import com.guanshiyun.controller.warehouse.vo.WarehouseVO;
import com.guanshiyun.repository.warehouse.WarehouseRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.warehouse.WarehouseService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.warehouse.Warehouse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final MyBigInteger myBigInteger;
    private final DatabaseClient databaseClient;
    private   final R2dbcEntityTemplate r2dbcEntityTemplate;
    //保存仓库信息,仓库ID为空则保存，非空则更新
    @Override
    public Mono<BigInteger> save(WarehouseSaveVO warehouseSaveVO) {
        Warehouse warehouse = BeanUtil.toBean(warehouseSaveVO, Warehouse.class);
        return Mono.deferContextual(ctx->{
            BigInteger useId =
                    myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            if(Objects.isNull(useId))
                return Mono.error(new Exception("用户不存在"));
            if(Objects.isNull(warehouse.getId()))
              return warehouseRepository.save( warehouse)
                        .map(Warehouse::getId);
            return r2dbcUpdateHelper.updateIgnoreNull(
                    EntityTableNameUtils.getName(Warehouse.class)
                            ,warehouse,
                            Warehouse.Fields.id
                    )
                    .onErrorResume(throwable ->{
                        log.error("更新仓库信息失败", throwable);
                        return Mono.error(new Exception("更新仓库信息失败"));
                    });
        })
                .onErrorResume(throwable ->{
                    log.error("保存仓库信息失败", throwable);
                    return Mono.error(new Exception("保存仓库信息失败"));
                });

    }

    @Override
    public Mono<Long> deleteById(BigInteger id) {
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
    public Mono< Long> deleteAllById(Collection<BigInteger> ids) {
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
    BigInteger pageNum = chatRecordRequestPage.getPageNum();
    //每页数量
    Integer pageSize = PageUtils.pageSize(chatRecordRequestPage.getPageSize());
    Warehouse warehouse = BeanUtil.toBean(requestPage.getCondition(), Warehouse.class);
    RequestPage<Warehouse> page= RequestPage.<Warehouse>builder()
            .condition(warehouse)
            .pageNum(pageNum)
            .pageSize(pageSize)
            .build();
    return Mono.deferContextual(ctx->{
                BigInteger userId =
                        myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
                if(Objects.isNull(userId))
                    return Mono.error(new Exception("用户不存在"));
                return ReactivePageQuery.of(r2dbcEntityTemplate,
                        Warehouse.class,page)
                        .like(Warehouse.Fields.name, requestPage.getCondition().getName())
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
    public Mono<WarehouseVO> findById(BigInteger id) {
        return warehouseRepository.findById(id)
                .map(warehouse -> BeanUtil.toBean(warehouse, WarehouseVO.class));
    }
}
