package com.guanshiyun;

import com.guanshiyun.controller.product.vo.ProductVO;
import com.guanshiyun.controller.warehouse.vo.WarehouseSaveVO;
import com.guanshiyun.controller.warehouse.vo.WarehouseVO;
import com.guanshiyun.requestpojo.RequestCursorPage;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.service.product.ProductService;
import com.guanshiyun.service.warehouse.WarehouseService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import com.guanshiyun.warehouse.Warehouse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class WarehouseAppApplicationTest {
    @Autowired
    private WarehouseService warehouseService;
    @Test
    void testCreateWarehouses() {
        warehouseService.saveAll(
                createWarehouses()
                        .stream().map(w-> BeanConvertUtil.toBean(w, Warehouse.class))
                        .toList()
        ).subscribe(i->{
            System.out.println("保存成功："+i);
        });
    }

    @Test
    public void test1Del(){
        warehouseService.deleteById(Long.valueOf(19))
                .subscribe();
    }
    @Test
    public void test2(){
        warehouseService.findPage(
                RequestPage.<WarehouseVO>builder()
                        .pageNum(Long.valueOf(1))
                        .pageSize(10)
                        .build()
        ) .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, Long.valueOf(1)))
                .onErrorResume(e->{
                    System.out.println("异常："+e.getMessage());
                    return Mono.error( e);
                })
                .subscribe(r->{
            System.out.println("总页数："+r.getTotal());
           r.getRows().forEach(System.out::println);
        });
    }
    @Test
    public void test3(){
       warehouseService.save(WarehouseSaveVO.builder()
                       .id(Long.valueOf(1))
                       .address("上海市浦东新区3号")
                       .name("上海仓")
                       .adminId(Long.valueOf(1))
                       .capacity(1000)
                       .status((short)1)
                       .build()
       )
               .contextWrite(Context.of(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY, Long.valueOf(1)))
               .subscribe(System.out::println);
    }





    public static List<WarehouseSaveVO> createWarehouses() {
        List<WarehouseSaveVO> warehouses = new ArrayList<>();

        warehouses.add(WarehouseSaveVO.builder()
                .name("乐购仓")
                .address("上海市浦东新区1号")
                .capacity(1000)
                .status((short)1)
                .adminId(Long.valueOf(101))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("京享仓")
                .address("北京市朝阳区2号")
                .capacity(1200)
                .status((short)1)
                .adminId(Long.valueOf(102))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("闪送仓")
                .address("广州市天河区3号")
                .capacity(1500)
                .status((short)1)
                .adminId(Long.valueOf(103))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("蜂巢仓")
                .address("深圳市南山区4号")
                .capacity(800)
                .status((short)1)
                .adminId(Long.valueOf(104))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("安达仓")
                .address("杭州市西湖区5号")
                .capacity(900)
                .status((short)1)
                .adminId(Long.valueOf(105))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("星品仓")
                .address("南京市鼓楼区6号")
                .capacity(1100)
                .status((short)1)
                .adminId(Long.valueOf(106))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("易购仓")
                .address("成都市锦江区7号")
                .capacity(1300)
                .status((short)1)
                .adminId(Long.valueOf(107))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("乐购汇仓")
                .address("武汉市武昌区8号")
                .capacity(950)
                .status((short)1)
                .adminId(Long.valueOf(108))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("顺丰源仓")
                .address("西安市雁塔区9号")
                .capacity(1000)
                .status((short)1)
                .adminId(Long.valueOf(109))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("快递通仓")
                .address("沈阳市和平区10号")
                .capacity(1400)
                .status((short)1)
                .adminId(Long.valueOf(110))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("瑞丰仓")
                .address("重庆市渝中区11号")
                .capacity(1250)
                .status((short)1)
                .adminId(Long.valueOf(111))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("汇通仓")
                .address("苏州市工业园区12号")
                .capacity(1100)
                .status((short)1)
                .adminId(Long.valueOf(112))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("腾达仓")
                .address("宁波市海曙区13号")
                .capacity(1150)
                .status((short)1)
                .adminId(Long.valueOf(113))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("天翼仓")
                .address("厦门市思明区14号")
                .capacity(1050)
                .status((short)1)
                .adminId(Long.valueOf(114))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("丰源仓")
                .address("郑州市金水区15号")
                .capacity(1200)
                .status((short)1)
                .adminId(Long.valueOf(115))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("速达仓")
                .address("长沙市岳麓区16号")
                .capacity(1000)
                .status((short)1)
                .adminId(Long.valueOf(116))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("嘉禾仓")
                .address("青岛市市南区17号")
                .capacity(950)
                .status((short)1)
                .adminId(Long.valueOf(117))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("乐汇仓")
                .address("大连市中山区18号")
                .capacity(1100)
                .status((short)1)
                .adminId(Long.valueOf(118))
                .build());

        warehouses.add(WarehouseSaveVO.builder()
                .name("星程仓")
                .address("天津市和平区19号")
                .capacity(1300)
                .status((short)1)
                .adminId(Long.valueOf(119))
                .build());

        return warehouses;
    }

    @Autowired
    private ProductService productService;
    @Test
    void test56(){
        productService.findCursorListProductVO(RequestCursorPage.<ProductVO>builder().pageSize(10).build())
                .doOnSuccess(productVOList -> System.out.println(productVOList))
                .block();
    }

}
