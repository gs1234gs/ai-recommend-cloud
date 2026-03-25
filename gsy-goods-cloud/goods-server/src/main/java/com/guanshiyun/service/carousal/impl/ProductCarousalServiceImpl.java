package com.guanshiyun.service.carousal.impl;

import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.carousal.ProductCarousal;
import com.guanshiyun.controller.carousal.vo.ProductCarousalSaveVO;
import com.guanshiyun.controller.carousal.vo.ProductCarousalVO;
import com.guanshiyun.mylong.MyLong;
import com.guanshiyun.repository.carousal.ProductCarousalRepository;
import com.guanshiyun.service.carousal.ProductCarousalService;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCarousalServiceImpl implements ProductCarousalService {
    private final ProductCarousalRepository productCarousalRepository;
    private final MyLong myLong;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;

    @Override
    public Mono<List<ProductCarousalVO>> findAll() {
        return productCarousalRepository.findAll(Sort.sort(ProductCarousal.class).by(ProductCarousal::getCreateTime).descending())
                .map(p-> BeanConvertUtil.toBean(p, ProductCarousalVO.class))
                .collectList()
//                .map(list-> {
//                    Collections.shuffle(list);
//                    return list;
//                })
                .onErrorResume(e-> {
                    log.error("查询所有轮播图失败", e);
                   return Mono.just(List.of());
                });
    }

    @Override
    public Mono<ProductCarousalVO> findById(Long id) {
        return productCarousalRepository.findById(id)
                .map(p-> BeanConvertUtil.toBean(p, ProductCarousalVO.class))
                .onErrorResume(e-> {
                    log.error("查询轮播图失败", e);
                   return Mono.empty();
                });
    }

    @Override
    public Mono<ProductCarousalVO> save(ProductCarousalSaveVO productCarousalSaveVO) {
        return Mono.deferContextual(ctx->{
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            Long userId = myLong.LongOrNull(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            ProductCarousal productCarousal =
                    BeanConvertUtil.toBean(productCarousalSaveVO, ProductCarousal.class);
            if(Objects.isNull(productCarousalSaveVO.getId())) {
                productCarousal.setCreator(userId)
                        .setCreateTime(LocalDateTime.now());
                return productCarousalRepository.save(productCarousal)
                        .mapNotNull(p-> BeanConvertUtil.toBean(p, ProductCarousalVO.class))
                        .onErrorResume(e-> {
                            log.error("保存轮播图失败", e);
                            return Mono.error(new RuntimeException("保存轮播图失败", e));
                        });
            }
                productCarousal.setUpdater(userId)
                        .setUpdateTime(LocalDateTime.now());
            return r2dbcUpdateHelper.updateIgnoreNull(
                    EntityTableNameUtils.getName(ProductCarousal.class),
                            productCarousal,
                            ProductCarousal.Fields.id)
                    .flatMap(productCarousalRepository::findById)
                    .mapNotNull(p-> BeanConvertUtil.toBean(p, ProductCarousalVO.class))
                    .onErrorResume(e-> {
                        log.error("保存轮播图失败", e);
                        return Mono.error(new RuntimeException("保存轮播图失败", e));
                    });
        });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.deferContextual(ctx->{
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            return productCarousalRepository.deleteById(id);
        });
    }

    @Override
    public Mono<Void> deleteByIds(List<Long> ids) {
        return Mono.deferContextual(ctx->{
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY)) {
                return Mono.error(new RuntimeException("用户未登录"));
            }
            return productCarousalRepository.deleteAllById(ids);
        });
    }

    @Override
    public Mono<ProductCarousalSaveVO> update(ProductCarousalSaveVO productCarousalSaveVO) {
        return productCarousalRepository.save(Objects.requireNonNull(BeanConvertUtil.toBean(productCarousalSaveVO, ProductCarousal.class)))
                .mapNotNull(p-> BeanConvertUtil.toBean(p, ProductCarousalSaveVO.class))
                .onErrorResume(e-> {
                    log.error("更新轮播图失败", e);
                   return Mono.empty();
                });
    }

    @Override
    public Mono<List<ProductCarousalVO>> findByType(Integer type) {
        return productCarousalRepository.findByType(type)
                .mapNotNull(p-> BeanConvertUtil.toBean(p, ProductCarousalVO.class))
                .collectList()
                .mapNotNull(list-> {
                    Collections.shuffle(list);
                    return list;
                })
                .onErrorResume(e-> {
                    log.error("查询轮播图失败", e);
                   return Mono.just(List.of());
                });
    }
}
