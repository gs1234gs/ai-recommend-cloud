package com.guanshiyun.service.category.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.ReactivePageQuery;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.biginteger.MyBigInteger;
import com.guanshiyun.category.Category;
import com.guanshiyun.controller.category.vo.CategorySaveVO;
import com.guanshiyun.controller.category.vo.CategoryVO;
import com.guanshiyun.repository.category.CategoryRepository;
import com.guanshiyun.repository.relation.ProductCategoryRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.category.CategoryService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import com.guanshiyun.utils.BeanConvertUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final MyBigInteger myBigInteger;
    private final ProductCategoryRepository productCategoryRepository;
    private final SnowflakePermanent snowflakePermanent;

    /**
 * 添加 类型
 * */
    @Override
    public Mono<BigInteger> save(CategorySaveVO categorySaveVO) {
        Category category = BeanUtil.toBean(categorySaveVO, Category.class).setCode(snowflakePermanent.stringNextId());
        return Mono.deferContextual(ctx->{
            if(!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY))
                return Mono.error(new RuntimeException("用户未登录"));
            BigInteger userId = myBigInteger.bigInteger(ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY));
            if(Objects.nonNull(category.getId())){
                category.setUpdater(userId);
                category.setUpdateTime(LocalDateTime.now());
                return r2dbcUpdateHelper.updateIgnoreNull(
                        EntityTableNameUtils.getName(Category.class),
                        category,
                        Category.Fields.id
                );
            }
            category.setCreator(userId);
            category.setCreateTime(LocalDateTime.now());
           return categoryRepository.save(category)
                    .flatMap(save->Mono.just(save.getId()))
                    .onErrorResume(e->{
                        log.error("添加类型失败：",e);
                        return Mono.error(new RuntimeException("添加类型失败"));
                    });
        });
    }

    @Override
    public Mono<Void> deleteById(BigInteger id) {
        return categoryRepository.deleteById(id);
    }

    @Override
    public Mono<Category> fndById(BigInteger id) {
        return categoryRepository.findById(id);
    }

//    @Override
//    public Mono<PageResultT<List<CategoryVO>>> findAllByPage( RequestPage<CategoryVO> requestPage) {
//        RequestPage<CategoryVO> categoryRequestPage = PageUtils.pageValidation(requestPage, CategoryVO.class);
//        Integer pageSize = categoryRequestPage.getPageSize();
//        BigInteger pageNum = categoryRequestPage.getPageNum();
//        CategoryVO conditionVO = categoryRequestPage.getCondition();
//        Category condition = BeanUtil.toBean(conditionVO, Category.class);
//        String name = condition.getName();// 添加用户权限
//        // 计算 offset
//        long offset = pageNum.subtract(BigInteger.ONE)
//                .multiply(BigInteger.valueOf(pageSize))
//                .longValue();
//        Criteria criteria = Criteria.empty();
//        criteria= criteria.and(Category.Fields.name).like( SqlConst.PERCENT+name+SqlConst.PERCENT);
//        Query dataQuery = Query.query(criteria)
//                .sort(Sort.by(Sort.Order.desc(BasePojo.Fields.createTime)))
//                .offset(offset)
//                .limit(pageSize);
//        Query countQuery = Query.query(criteria);
//
//        return r2dbcEntityTemplate.select(countQuery,Category.class)
//                .count()
//                .flatMap(count ->
//                        r2dbcEntityTemplate.select(dataQuery,Category.class)
//                                .map(item->BeanUtil.toBean(item,CategoryVO.class))
//                                .collectList()
//                                .map(rows -> PageResultT.<List<CategoryVO>>builder()
//                                        .total(count)
//                                        .rows( rows)
//                                        .build()
//                                )
//
//                );
//    }
@Override
public Mono<PageResultT<List<CategoryVO>>> findAllByPage( RequestPage<CategoryVO> requestPage) {
    RequestPage<Category> page = BeanConvertUtil.toBean(requestPage, Category.class);
    return ReactivePageQuery.of(r2dbcEntityTemplate, Category.class, page)
            .like(Category.Fields.name, requestPage.getCondition().getName())
            .page()
            .map(pageResultT ->
                    BeanConvertUtil.toBean(pageResultT, CategoryVO.class));
}

    @Override
    public Mono<List<CategoryVO>> findAll() {
        return categoryRepository.findAll()
                .mapNotNull(item-> BeanConvertUtil.toBean(item,CategoryVO.class))
                .collectList();
    }

    @Override
    public Flux<CategoryVO> findByProductId(BigInteger productId) {
        return productCategoryRepository.findByProductId(productId)
                .flatMap(productCategory->categoryRepository.findById(productCategory.getCategoryId()))
                .mapNotNull(item->BeanConvertUtil.toBean(item,CategoryVO.class));
    }
}
