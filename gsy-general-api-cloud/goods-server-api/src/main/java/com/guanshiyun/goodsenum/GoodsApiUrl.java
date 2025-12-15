package com.guanshiyun.goodsenum;

public class GoodsApiUrl {
    // 获取类型
    public static final String CATEGORY_FIND_BY_ALL = "/category/findAll";
    // 获取产品列表
    public static final String PRODUCT_FIND_CURSOR = "/product/findCursorList";
    //获取sku列表
    public static final String SKU_FIND_PAGE = "/sku/findPage";
    //获取属性列表
    public static final String SKU_FIND_PRODUCT = "/sku/findByProductId/{productId}";
    //获取标签列表
    public static final String TAG_FIND_CURSOR = "/tag/findPage";
    //获取仓库列表
    public static final String WAREHOUSE_FIND_CURSOR = "/warehouse/findPage";
    //根据id获取标签
    public static final String TAG_FIND_BY_ID = "/tag/findById";
    //根据商品id获取标签
    public static final String TAG_FIND_BY_PRODUCT_ID = "/tag/findByProductId/{productId}";
    //根据id获取仓库
    public static final String WAREHOUSE_FIND_BY_ID = "/warehouse/findById";
    //根据商品id获取仓库
    public static final String WAREHOUSE_FIND_BY_PRODUCT_ID = "/warehouse/findByProductId";
    //获取所有仓库
    public static final String WAREHOUSE_FIND_ALL = "/warehouse/findAll";
    //商品id获取分类
    public static final String CATEGORY_FIND_BY_PRODUCT_ID = "/category/findByProductId/{productId}";
}
