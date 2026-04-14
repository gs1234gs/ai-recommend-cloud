package com.guanshiyun.controller.warehouse;

import com.guanshiyun.controller.warehouse.vo.WarehouseSaveVO;
import com.guanshiyun.controller.warehouse.vo.WarehouseVO;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.responsepojo.ResultT;
import com.guanshiyun.service.warehouse.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/warehouse/")
public class WarehouseController {
    private final WarehouseService warehouseService;
    //添加仓库或者修改仓库
    @PostMapping("save")
    public Mono<ResultT<Long>> save( @RequestBody WarehouseSaveVO warehouseSaveVO){
        return warehouseService.save(warehouseSaveVO)
                .map(warehouseId->
                {
                    log.info("保存仓库成功，仓库ID为：{}",warehouseId);
                    return ResultT.<Long>builder()
                            .code(HttpStatus.OK.value())
                            .msg("保存成功")
                            .data(warehouseId)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.info("保存仓库失败", throwable);
                    return Mono.just(ResultT.<Long>builder()
                            .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .msg("保存失败")
                            .build());
                });
    }
    /**
     * 删除
     * */
    @DeleteMapping("deleteById/{id}")
    public Mono<ResultT<Long>> deleteById(@PathVariable Long id){
        return warehouseService.deleteById(id)
                .map(deleteCount->ResultT.<Long>builder()
                        .code(HttpStatus.OK.value())
                        .msg("删除成功")
                        .data(deleteCount)
                        .build())
                .onErrorResume(throwable ->{
                    log.info("删除仓库失败", throwable);
                    return Mono.just(
                            ResultT.<Long>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("删除失败")
                                    .build()
                    );
                });
    }
    /**
     * 批量删除
     * */
    @DeleteMapping("deleteAllByIds")
    public Mono<ResultT<Long>> deleteByIds(@RequestBody Collection<Long> ids){
        return warehouseService.deleteAllById(ids)
                .map(deleteCount->ResultT.<Long>builder()
                        .code(HttpStatus.OK.value())
                        .msg("删除成功")
                        .data(deleteCount)
                        .build())
                .onErrorResume(throwable ->{
                    log.info("批量删除仓库失败", throwable);
                    return Mono.just(
                            ResultT.<Long>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("批量删除失败")
                                    .build()
                    );
                });
    }
    /**
     * 分页查询
     * */
    @PostMapping("findPage")
    public Mono<ResultT<PageResultT<List<WarehouseVO>>>>findPage( @RequestBody RequestPage<WarehouseVO> requestPage){
        return warehouseService.findPage(requestPage)
                .map(warehousePageResultT->
                {
                    log.info("分页查询仓库成功");
                    return ResultT.<PageResultT<List<WarehouseVO>>>builder()
                            .code(HttpStatus.OK.value())
                            .msg("查询成功")
                            .data(warehousePageResultT)
                            .build();
                })
                .onErrorResume(throwable ->{
                    log.info("分页查询仓库失败", throwable);
                    return Mono.just(
                            ResultT.<PageResultT<List<WarehouseVO>>>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("分页查询失败")
                                    .build()
                    );
                });
    }
    @GetMapping("findById/{id}")
    public Mono<ResultT<WarehouseVO>> findById(@PathVariable Long id){
        return warehouseService.findById(id)
                .map(warehouseVO->
                {
                    log.info("查询仓库成功");
                    return ResultT.<WarehouseVO>builder()
                            .code(HttpStatus.OK.value())
                            .msg("查询成功")
                            .data(warehouseVO)
                            .build();
                })
                .onErrorResume(e->{
                    log.info("查询仓库失败", e);
                    return Mono.just(
                            ResultT.<WarehouseVO>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("查询失败")
                                    .build()
                    );
                });
    }
@GetMapping("findAll")
    public Mono<ResultT<List<WarehouseVO>>> findAll(){
        return warehouseService.findAll()
                .map(warehouseVOList->
                {
                    log.info("查询仓库成功");
                    return ResultT.<List<WarehouseVO>>builder()
                            .code(HttpStatus.OK.value())
                            .msg("查询成功")
                            .data(warehouseVOList)
                            .build();
                })
                .onErrorResume(e->{
                    log.info("查询仓库失败", e);
                    return Mono.just(
                            ResultT.<List<WarehouseVO>>builder()
                                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                    .msg("查询失败")
                                    .build()
                    );
                });
    }

}
