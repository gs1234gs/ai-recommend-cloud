package com.guanshiyun.service.tag.impl;

import cn.hutool.core.bean.BeanUtil;
import com.db.cursorQuery.ReactivePageQuery;
import com.db.dbnumber.ConstNumber;
import com.db.r2dbcupdate.R2dbcUpdateHelper;
import com.db.tablename.EntityTableNameUtils;
import com.guanshiyun.controller.tag.vo.TagSaveVO;
import com.guanshiyun.controller.tag.vo.TagVO;
import com.guanshiyun.repository.relation.ProductTagRepository;
import com.guanshiyun.repository.tag.TagRepository;
import com.guanshiyun.requestpojo.RequestPage;
import com.guanshiyun.responsepojo.PageResultT;
import com.guanshiyun.service.tag.TagService;
import com.guanshiyun.snowflake.SnowflakePermanent;
import com.guanshiyun.tag.Tag;
import com.guanshiyun.threadcontext.ThreadSecurityLocalKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final R2dbcUpdateHelper r2dbcUpdateHelper;
    private final ProductTagRepository productTagRepository;
    private final SnowflakePermanent snowflakePermanent;

    @Override
    public Mono<BigInteger> save(TagSaveVO tagSaveVO) {
        Tag tag = BeanUtil.toBean(tagSaveVO, Tag.class);
        LocalDateTime now = LocalDateTime.now();
        return Mono.deferContextual(ctx -> {
            if (!ctx.hasKey(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY))
                return Mono.error(new Throwable("用户未登录"));
            BigInteger userId = ctx.get(ThreadSecurityLocalKey.THREAD_SECURITY_LOCAL_USER_ID_KEY);
            if (Objects.isNull(tag.getId())) {
                String code = snowflakePermanent.stringNextId();
                tag.setCode(tagSaveVO.getCode() + code)
                        .setCreator(userId)
                        .setUpdateTime(now);
                return tagRepository.save(tag)
                        .map(Tag::getId);
            }
            tag.setUpdater(userId);
            tag.setUpdateTime(now);
            return r2dbcUpdateHelper.updateIgnoreNull(
                    EntityTableNameUtils.getName(Tag.class),
                    tag,
                    Tag.Fields.id
            );
        });
    }

    @Override
    public Mono<Void> deleteById(BigInteger id) {
        return tagRepository.deleteById(id)
                .onErrorResume(e -> {
                    log.error("删除失败", e);
                    return Mono.error(new Exception("删除失败", e));
                });
    }

    @Override
    public Mono<TagVO> findById(BigInteger id) {
        return tagRepository.findById(id)
                .map(tag -> BeanUtil.toBean(tag, TagVO.class));
    }

    @Override
    public Mono<PageResultT<List<TagVO>>> findAllByPage(RequestPage<TagVO> requestPage) {
        return ReactivePageQuery.of(
                        r2dbcEntityTemplate,
                        Tag.class,
                        RequestPage.<Tag>builder()
                                .condition(BeanUtil.toBean(
                                        requestPage.getCondition(),
                                        Tag.class)
                                )
                                .pageNum(requestPage.getPageNum())
                                .pageSize(requestPage.getPageSize())
                                .build())
                .page()
                .map(page -> {
                    log.info("查询成功");
                    return PageResultT.<List<TagVO>>builder()
                            .pageNum(page.getPageNum())
                            .pageSize(page.getPageSize())
                            .total(page.getTotal())
                            .rows(page.getRows().stream()
                                    .map(tag -> BeanUtil.toBean(tag, TagVO.class))
                                    .toList()
                            )
                            .build();
                })
                .onErrorResume(e -> {
                    log.error("查询失败", e);
                    return Mono.just(PageResultT.<List<TagVO>>builder()
                            .pageNum(requestPage.getPageNum())
                            .pageSize(requestPage.getPageSize())
                            .total(ConstNumber.LONG_ZERO)
                            .rows(List.of())
                            .build());
                });
    }

    @Override
    public Mono<Void> deleteAllById(List<BigInteger> ids) {
        return tagRepository.deleteAllById(ids);
    }

    @Override
    public Mono<List<TagVO>> findTagByProductId(BigInteger productId) {
        return productTagRepository.findTagIdByProductId(productId)

                .flatMap(tagRepository::findById)
                .collectList()
                .map(tags -> tags.stream()
                        .map(tag -> BeanUtil.toBean(tag, TagVO.class))
                        .toList()
                );

    }

    @Override
    public Mono<List<TagVO>> findTagByProductId(List<BigInteger> productIds) {
        return null;
    }
}
