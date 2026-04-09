# AI推荐云平台接口文档

> 本文档按照微服务模块划分，包含所有服务的API接口说明

---

## 一、系统服务 (gsy-system-cloud)

系统服务负责用户认证、用户管理、角色权限、租户管理等核心功能。

### 1.1 用户认证接口

#### 1.1.1 登录
- **功能名称**：登录
- **请求方式**：POST
- **请求路径**：`/signInUp/signIn`
- **请求体**：
```json
{
  "username": "string",
  "password": "string"
}
```
- **请求示例**：
```json
{
  "username": "admin",
  "password": "123456"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 1.1.2 注册
- **功能名称**：注册
- **请求方式**：POST
- **请求路径**：`/signInUp/signUp`
- **请求体**：
```json
{
  "username": "string",
  "password": "string",
  "nickName": "string",
  "email": "string",
  "phoneNumber": "string"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "注册成功",
  "data": "注册成功"
}
```

#### 1.1.3 退出登录
- **功能名称**：退出登录
- **请求方式**：GET
- **请求路径**：`/logout`
- **请求参数**：无
- **响应示例**：
```json
{
  "code": 200,
  "msg": "退出成功！",
  "data": 1
}
```

### 1.2 系统用户管理接口

#### 1.2.1 添加用户
- **功能名称**：添加用户
- **请求方式**：POST
- **请求路径**：`/sysUser/save`
- **请求体**：
```json
{
  "username": "string",
  "password": "string",
  "nickName": "string",
  "email": "string",
  "phoneNumber": "string",
  "deptId": 0,
  "type": 0,
  "gender": 0,
  "status": 0,
  "idCard": "string",
  "image": "string",
  "remark": "string",
  "tenantId": 1
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": 123
}
```

#### 1.2.2 删除用户
- **功能名称**：删除用户
- **请求方式**：DELETE
- **请求路径**：`/sysUser/deleteById/{id}`
- **路径参数**：`id` (Long) - 用户ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除用户成功",
  "data": 1
}
```

#### 1.2.3 批量删除用户
- **功能名称**：批量删除用户
- **请求方式**：DELETE
- **请求路径**：`/sysUser/deleteUserByIds`
- **请求体**：
```json
[1, 2, 3]
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "批量删除用户成功",
  "data": 3
}
```

#### 1.2.4 获取当前用户信息
- **功能名称**：获取当前用户信息
- **请求方式**：GET
- **请求路径**：`/sysUser/findById`
- **请求参数**：无（从Token中获取用户ID）
- **响应示例**：
```json
{
  "code": 200,
  "msg": "获取用户成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickName": "管理员",
    "email": "admin@example.com",
    "phoneNumber": "13800138000"
  }
}
```

#### 1.2.5 根据ID获取用户
- **功能名称**：根据ID获取用户
- **请求方式**：GET
- **请求路径**：`/sysUser/findById/{id}`
- **路径参数**：`id` (Long) - 用户ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "获取用户成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickName": "管理员"
  }
}
```

#### 1.2.6 修改用户
- **功能名称**：修改用户
- **请求方式**：PUT
- **请求路径**：`/sysUser/updateUserById`
- **请求体**：
```json
{
  "id": 1,
  "username": "string",
  "nickName": "string",
  "email": "string",
  "phoneNumber": "string",
  "status": 0,
  "deptId": 0,
  "gender": 0
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "修改用户成功",
  "data": 1
}
```

#### 1.2.7 分页查询用户
- **功能名称**：分页查询用户
- **请求方式**：POST
- **请求路径**：`/sysUser/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "username": "admin",
    "status": 0
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "获取用户列表成功",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "username": "admin",
        "nickName": "管理员"
      }
    ]
  }
}
```

#### 1.2.8 更新当前登录用户信息
- **功能名称**：更新当前登录用户信息
- **请求方式**：PUT
- **请求路径**：`/sysUser/updateSignInUser`
- **请求体**：
```json
{
  "nickName": "string",
  "email": "string",
  "phoneNumber": "string",
  "image": "string"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "更新成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickName": "新昵称"
  }
}
```

#### 1.2.9 获取当前登录用户信息
- **功能名称**：获取当前登录用户信息
- **请求方式**：GET
- **请求路径**：`/sysUser/findBySignInUserId`
- **请求参数**：无
- **响应示例**：
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickName": "管理员"
  }
}
```

### 1.3 租户管理接口

#### 1.3.1 添加租户
- **功能名称**：添加租户
- **请求方式**：POST
- **请求路径**：`/tenant/save`
- **请求体**：
```json
{
  "name": "string",
  "contactName": "string",
  "contactPhone": "string",
  "email": "string",
  "status": 0
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": 123
}
```

#### 1.3.2 删除租户
- **功能名称**：删除租户
- **请求方式**：DELETE
- **请求路径**：`/tenant/deleteById/{id}`
- **路径参数**：`id` (Long) - 租户ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": true
}
```

#### 1.3.3 分页查询租户
- **功能名称**：分页查询租户
- **请求方式**：POST
- **请求路径**：`/tenant/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "name": "租户名称"
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 10,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "name": "租户A"
      }
    ]
  }
}
```

#### 1.3.4 根据ID查询租户
- **功能名称**：根据ID查询租户
- **请求方式**：GET
- **请求路径**：`/tenant/findById/{id}`
- **路径参数**：`id` (Long) - 租户ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "name": "租户A"
  }
}
```

### 1.4 角色管理接口

#### 1.4.1 添加/修改角色
- **功能名称**：添加/修改角色
- **请求方式**：POST
- **请求路径**：`/sysRole/save`
- **请求体**：
```json
{
  "id": 1,
  "roleName": "角色名称",
  "roleCode": "ROLE_ADMIN",
  "description": "角色描述",
  "status": 0
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": 123
}
```

#### 1.4.2 删除角色
- **功能名称**：删除角色
- **请求方式**：DELETE
- **请求路径**：`/sysRole/deleteById/{id}`
- **路径参数**：`id` (Long) - 角色ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": 1
}
```

#### 1.4.3 修改角色
- **功能名称**：修改角色
- **请求方式**：PUT
- **请求路径**：`/sysRole/updateById`
- **请求体**：
```json
{
  "id": 1,
  "roleName": "新角色名称",
  "description": "新描述",
  "status": 0
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "修改成功",
  "data": 1
}
```

#### 1.4.4 分页查询角色
- **功能名称**：分页查询角色
- **请求方式**：POST
- **请求路径**：`/sysRole/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "roleName": "角色名称",
    "status": 0
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "获取用户列表成功",
  "data": {
    "total": 10,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "roleName": "管理员",
        "roleCode": "ROLE_ADMIN"
      }
    ]
  }
}
```

#### 1.4.5 根据用户ID查询角色列表
- **功能名称**：根据用户ID查询角色列表
- **请求方式**：GET
- **请求路径**：`/sysRole/findRoleListByUserId/{userId}`
- **路径参数**：`userId` (Long) - 用户ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "获取角色列表成功",
  "data": [
    {
      "id": 1,
      "roleName": "管理员",
      "roleCode": "ROLE_ADMIN"
    }
  ]
}
```

#### 1.4.6 根据ID查询角色
- **功能名称**：根据ID查询角色
- **请求方式**：GET
- **请求路径**：`/sysRole/findById/{id}`
- **路径参数**：`id` (Long) - 角色ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "获取角色成功",
  "data": {
    "id": 1,
    "roleName": "管理员",
    "roleCode": "ROLE_ADMIN",
    "description": "系统管理员"
  }
}
```

### 1.5 菜单管理接口

#### 1.5.1 获取当前用户菜单
- **功能名称**：获取当前用户菜单
- **请求方式**：GET
- **请求路径**：`/sysMenu/userId`
- **请求参数**：无（从Token中获取用户ID）
- **响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "menuName": "首页",
      "parentId": 0,
      "path": "/home",
      "icon": "home",
      "sort": 1,
      "children": []
    }
  ]
}
```

#### 1.5.2 根据ID查询菜单
- **功能名称**：根据ID查询菜单
- **请求方式**：GET
- **请求路径**：`/sysMenu/findById/{id}`
- **路径参数**：`id` (Long) - 菜单ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "id": 1,
    "menuName": "首页",
    "parentId": 0,
    "path": "/home",
    "icon": "home",
    "sort": 1
  }
}
```

#### 1.5.3 获取用户菜单
- **功能名称**：获取用户菜单
- **请求方式**：GET
- **请求路径**：`/sysMenu/findByUserId`
- **请求参数**：无（从Token中获取用户ID）
- **响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "menuName": "系统管理",
      "parentId": 0,
      "children": [
        {
          "id": 2,
          "menuName": "用户管理",
          "parentId": 1,
          "path": "/system/user"
        }
      ]
    }
  ]
}
```

#### 1.5.4 根据父级ID查询菜单
- **功能名称**：根据父级ID查询菜单
- **请求方式**：GET
- **请求路径**：`/sysMenu/findByParentId/{id}`
- **路径参数**：`id` (Long) - 父级菜单ID（可选）
- **响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "menuName": "系统管理",
      "parentId": 0,
      "children": []
    }
  ]
}
```

#### 1.5.5 删除菜单
- **功能名称**：删除菜单
- **请求方式**：DELETE
- **请求路径**：`/sysMenu/deleteById/{id}`
- **路径参数**：`id` (Long) - 菜单ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": 1
}
```

#### 1.5.6 添加/修改菜单
- **功能名称**：添加/修改菜单
- **请求方式**：POST
- **请求路径**：`/sysMenu/save`
- **请求体**：
```json
{
  "id": 1,
  "menuName": "菜单名称",
  "parentId": 0,
  "path": "/path",
  "component": "component",
  "icon": "icon",
  "sort": 1,
  "type": 0,
  "status": 0
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": 123
}
```

#### 1.5.7 修改菜单
- **功能名称**：修改菜单
- **请求方式**：PUT
- **请求路径**：`/sysMenu/updateById`
- **请求体**：
```json
{
  "id": 1,
  "menuName": "新菜单名称",
  "path": "/new-path",
  "icon": "new-icon",
  "sort": 2
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "成功",
  "data": 1
}
```

---

## 二、商品服务 (gsy-goods-cloud)

商品服务负责商品管理、SKU管理、分类管理、标签管理、轮播图管理等。

### 2.1 商品管理接口

#### 2.1.1 添加商品
- **功能名称**：添加商品
- **请求方式**：POST
- **请求路径**：`/product/save`
- **请求体**：
```json
{
  "name": "商品名称",
  "description": "商品描述",
  "image": "商品图片URL",
  "video": "视频URL",
  "brand": "品牌",
  "placeOfOrigin": "产地",
  "level": 1,
  "salesVolume": 0,
  "status": 1,
  "publishTime": "2024-01-01T00:00:00",
  "offlineTime": "2024-12-31T23:59:59",
  "categoryId": [1, 2],
  "tagId": [1, 2, 3]
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": 123
}
```

#### 2.1.2 删除商品
- **功能名称**：删除商品
- **请求方式**：DELETE
- **请求路径**：`/product/deleteById/{id}`
- **路径参数**：`id` (Long) - 商品ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": 1
}
```

#### 2.1.3 批量删除商品
- **功能名称**：批量删除商品
- **请求方式**：DELETE
- **请求路径**：`/product/deleteAllById`
- **请求体**：
```json
[1, 2, 3, 4, 5]
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

#### 2.1.4 分页查询商品
- **功能名称**：分页查询商品
- **请求方式**：POST
- **请求路径**：`/product/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "name": "商品名称",
    "status": 1,
    "brand": "品牌"
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "name": "商品名称",
        "price": 99.99,
        "status": 1
      }
    ]
  }
}
```

#### 2.1.5 游标分页查询商品
- **功能名称**：游标分页查询商品
- **请求方式**：POST
- **请求路径**：`/product/findCursorList`
- **请求体**：
```json
{
  "cursor": "string",
  "limit": 10,
  "data": {}
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "list": [],
    "cursor": "next_cursor_string",
    "hasMore": true
  }
}
```

#### 2.1.6 根据ID查询商品
- **功能名称**：根据ID查询商品
- **请求方式**：GET
- **请求路径**：`/product/findById/{id}`
- **路径参数**：`id` (Long) - 商品ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "name": "商品名称",
    "description": "商品描述",
    "price": 99.99,
    "status": 1
  }
}
```

### 2.2 SKU管理接口

#### 2.2.1 添加SKU
- **功能名称**：添加SKU
- **请求方式**：POST
- **请求路径**：`/sku/save`
- **请求体**：
```json
{
  "productId": 1,
  "skuName": "SKU名称",
  "price": 99.99,
  "stock": 100,
  "attributes": "{\"color\": \"红色\", \"size\": \"XL\"}",
  "image": "SKU图片URL"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "添加成功",
  "data": 123
}
```

#### 2.2.2 删除SKU
- **功能名称**：删除SKU
- **请求方式**：DELETE
- **请求路径**：`/sku/deleteById/{id}`
- **路径参数**：`id` (Long) - SKU ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

#### 2.2.3 批量删除SKU
- **功能名称**：批量删除SKU
- **请求方式**：DELETE
- **请求路径**：`/sku/deleteAllById`
- **请求体**：
```json
[1, 2, 3]
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "批量删除成功",
  "data": null
}
```

#### 2.2.4 根据ID查询SKU
- **功能名称**：根据ID查询SKU
- **请求方式**：GET
- **请求路径**：`/sku/findById/{id}`
- **路径参数**：`id` (Long) - SKU ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "productId": 1,
    "skuName": "SKU名称",
    "price": 99.99,
    "stock": 100
  }
}
```

#### 2.2.5 分页查询SKU
- **功能名称**：分页查询SKU
- **请求方式**：POST
- **请求路径**：`/sku/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "productId": 1,
    "skuName": "SKU名称"
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 50,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "productId": 1,
        "skus": [
          {
            "id": 1,
            "skuName": "红色-XL",
            "price": 99.99,
            "stock": 50
          }
        ]
      }
    ]
  }
}
```

#### 2.2.6 根据商品ID查询SKU列表
- **功能名称**：根据商品ID查询SKU列表
- **请求方式**：GET
- **请求路径**：`/sku/findByProductId/{productId}`
- **路径参数**：`productId` (Long) - 商品ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "productId": 1,
      "skuName": "红色-XL",
      "price": 99.99,
      "stock": 50
    },
    {
      "id": 2,
      "productId": 1,
      "skuName": "蓝色-L",
      "price": 89.99,
      "stock": 30
    }
  ]
}
```

#### 2.2.7 根据SKU IDs查询
- **功能名称**：根据SKU IDs查询
- **请求方式**：GET
- **请求路径**：`/sku/findBySkuIds`
- **请求参数**：
  - `skuIds` (List<Long>) - SKU ID列表
- **请求示例**：`/sku/findBySkuIds?skuIds=1&skuIds=2&skuIds=3`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "skuName": "SKU-1",
      "price": 99.99
    }
  ]
}
```

#### 2.2.8 减少库存
- **功能名称**：减少库存
- **请求方式**：PUT
- **请求路径**：`/sku/reduceStockById`
- **请求参数**：
  - `id` (Long) - SKU ID
  - `count` (Integer) - 减少数量
- **请求示例**：`/sku/reduceStockById?id=1&count=5`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "修改库存成功",
  "data": true
}
```

#### 2.2.9 增加库存
- **功能名称**：增加库存
- **请求方式**：PUT
- **请求路径**：`/sku/addStockById`
- **请求参数**：
  - `id` (Long) - SKU ID
  - `count` (Integer) - 增加数量
- **请求示例**：`/sku/addStockById?id=1&count=10`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "修改库存成功",
  "data": true
}
```

#### 2.2.10 增加销量
- **功能名称**：增加销量
- **请求方式**：PUT
- **请求路径**：`/sku/addSalesById`
- **请求参数**：
  - `id` (Long) - SKU ID
  - `count` (Integer) - 增加数量
- **请求示例**：`/sku/addSalesById?id=1&count=2`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "修改销量成功",
  "data": true
}
```

### 2.3 分类管理接口

#### 2.3.1 添加/修改分类
- **功能名称**：添加/修改分类
- **请求方式**：POST
- **请求路径**：`/category/save`
- **请求体**：
```json
{
  "id": 1,
  "name": "分类名称",
  "parentId": 0,
  "level": 1,
  "sort": 1,
  "icon": "图标URL"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": 123
}
```

#### 2.3.2 删除分类
- **功能名称**：删除分类
- **请求方式**：DELETE
- **请求路径**：`/category/deleteById/{id}`
- **路径参数**：`id` (Long) - 分类ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

#### 2.3.3 根据ID查询分类
- **功能名称**：根据ID查询分类
- **请求方式**：GET
- **请求路径**：`/category/findById/{id}`
- **路径参数**：`id` (Long) - 分类ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "name": "分类名称",
    "parentId": 0,
    "level": 1
  }
}
```

#### 2.3.4 分页查询分类
- **功能名称**：分页查询分类
- **请求方式**：POST
- **请求路径**：`/category/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "name": "分类名称",
    "level": 1
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 20,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "name": "分类名称",
        "parentId": 0
      }
    ]
  }
}
```

### 2.4 标签管理接口

#### 2.4.1 添加标签
- **功能名称**：添加标签
- **请求方式**：POST
- **请求路径**：`/tag/save`
- **请求体**：
```json
{
  "name": "标签名称",
  "type": 1,
  "description": "标签描述"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "添加成功",
  "data": 123
}
```

#### 2.4.2 删除标签
- **功能名称**：删除标签
- **请求方式**：DELETE
- **请求路径**：`/tag/deleteById/{id}`
- **路径参数**：`id` (Long) - 标签ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

#### 2.4.3 根据ID查询标签
- **功能名称**：根据ID查询标签
- **请求方式**：GET
- **请求路径**：`/tag/findById/{id}`
- **路径参数**：`id` (Long) - 标签ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "name": "标签名称",
    "type": 1
  }
}
```

#### 2.4.4 分页查询标签
- **功能名称**：分页查询标签
- **请求方式**：POST
- **请求路径**：`/tag/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "name": "标签名称",
    "type": 1
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 30,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "name": "标签名称",
        "type": 1
      }
    ]
  }
}
```

### 2.5 轮播图管理接口

#### 2.5.1 保存轮播图
- **功能名称**：保存轮播图
- **请求方式**：POST
- **请求路径**：`/product/carousal/save`
- **请求体**：
```json
{
  "name": "轮播图名称",
  "image": "图片URL",
  "url": "跳转链接",
  "type": 1,
  "sort": 1,
  "status": 1
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": {
    "id": 1,
    "name": "轮播图名称",
    "image": "http://example.com/image.jpg"
  }
}
```

#### 2.5.2 获取所有轮播图
- **功能名称**：获取所有轮播图
- **请求方式**：GET
- **请求路径**：`/product/carousal/findAll`
- **请求参数**：无
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "name": "轮播图1",
      "image": "http://example.com/image1.jpg",
      "url": "http://example.com/product/1",
      "type": 1,
      "sort": 1
    }
  ]
}
```

#### 2.5.3 删除轮播图
- **功能名称**：删除轮播图
- **请求方式**：DELETE
- **请求路径**：`/product/carousal/deleteById/{id}`
- **路径参数**：`id` (Long) - 轮播图ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

#### 2.5.4 批量删除轮播图
- **功能名称**：批量删除轮播图
- **请求方式**：DELETE
- **请求路径**：`/product/carousal/deleteBatch`
- **请求体**：
```json
[1, 2, 3]
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

#### 2.5.5 根据ID查询轮播图
- **功能名称**：根据ID查询轮播图
- **请求方式**：GET
- **请求路径**：`/product/carousal/findById/{id}`
- **路径参数**：`id` (Long) - 轮播图ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "name": "轮播图名称",
    "image": "http://example.com/image.jpg"
  }
}
```

#### 2.5.6 根据类型查询轮播图
- **功能名称**：根据类型查询轮播图
- **请求方式**：GET
- **请求路径**：`/product/carousal/findByType/{type}`
- **路径参数**：`type` (Integer) - 轮播图类型
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "name": "首页轮播图",
      "image": "http://example.com/home.jpg",
      "type": 1
    }
  ]
}
```

### 2.6 仓库管理接口

#### 2.6.1 添加/修改仓库
- **功能名称**：添加/修改仓库
- **请求方式**：POST
- **请求路径**：`/warehouse/save`
- **请求体**：
```json
{
  "id": 1,
  "name": "仓库名称",
  "address": "仓库地址",
  "capacity": 10000,
  "status": 0,
  "adminId": 1
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": 123
}
```

#### 2.6.2 删除仓库
- **功能名称**：删除仓库
- **请求方式**：DELETE
- **请求路径**：`/warehouse/deleteById/{id}`
- **路径参数**：`id` (Long) - 仓库ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": 1
}
```

#### 2.6.3 批量删除仓库
- **功能名称**：批量删除仓库
- **请求方式**：DELETE
- **请求路径**：`/warehouse/deleteAllByIds`
- **请求体**：
```json
[1, 2, 3]
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": 3
}
```

#### 2.6.4 分页查询仓库
- **功能名称**：分页查询仓库
- **请求方式**：POST
- **请求路径**：`/warehouse/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "name": "仓库名称",
    "status": 0
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 10,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "name": "仓库名称",
        "address": "仓库地址",
        "capacity": 10000,
        "status": 0
      }
    ]
  }
}
```

#### 2.6.5 根据ID查询仓库
- **功能名称**：根据ID查询仓库
- **请求方式**：GET
- **请求路径**：`/warehouse/findById{id}`
- **路径参数**：`id` (Long) - 仓库ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "name": "仓库名称",
    "address": "仓库地址",
    "capacity": 10000,
    "status": 0,
    "adminId": 1
  }
}
```

#### 2.6.6 查询所有仓库
- **功能名称**：查询所有仓库
- **请求方式**：GET
- **请求路径**：`/warehouse/findAll`
- **请求参数**：无
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "name": "仓库名称",
      "address": "仓库地址",
      "capacity": 10000,
      "status": 0
    }
  ]
}
```

---

## 三、AI服务 (gsy-ai-cloud)

AI服务负责大模型管理、AI对话、向量检索等智能功能。

### 3.1 大模型管理接口

#### 3.1.1 添加/修改大模型
- **功能名称**：添加/修改大模型
- **请求方式**：POST
- **请求路径**：`/bigModel/save`
- **请求体**：
```json
{
  "id": 1,
  "name": "模型名称",
  "apiKey": "sk-xxx",
  "baseUrl": "https://api.openai.com",
  "model": "gpt-4",
  "maxTokens": 4096,
  "temperature": 0.7
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": 123
}
```

#### 3.1.2 删除大模型
- **功能名称**：删除大模型
- **请求方式**：DELETE
- **请求路径**：`/bigModel/deleteById/{id}`
- **路径参数**：`id` (Long) - 大模型ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": 1
}
```

#### 3.1.3 分页查询大模型
- **功能名称**：分页查询大模型
- **请求方式**：POST
- **请求路径**：`/bigModel/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "name": "模型名称"
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 5,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "name": "GPT-4",
        "model": "gpt-4"
      }
    ]
  }
}
```

#### 3.1.4 根据ID查询大模型
- **功能名称**：根据ID查询大模型
- **请求方式**：GET
- **请求路径**：`/bigModel/findById/{id}`
- **路径参数**：`id` (Long) - 大模型ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "name": "GPT-4",
    "apiKey": "sk-xxx",
    "baseUrl": "https://api.openai.com",
    "model": "gpt-4"
  }
}
```

### 3.2 AI对话接口

#### 3.2.1 一次性对话
- **功能名称**：一次性对话
- **请求方式**：POST
- **请求路径**：`/chat/chatAll`
- **请求体**：
```json
{
  "message": "你好，请介绍一下自己",
  "modelId": 1,
  "chatId": 1,
  "systemPrompt": "你是一个智能助手"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "对话成功",
  "data": "你好！我是一个智能助手..."
}
```

#### 3.2.2 流式对话
- **功能名称**：流式对话
- **请求方式**：POST
- **请求路径**：`/chat/fluxChat`
- **请求体**：
```json
{
  "message": "你好，请介绍一下自己",
  "modelId": 1,
  "chatId": 1,
  "systemPrompt": "你是一个智能助手"
}
```
- **响应头**：
  - `X-Conversation-ID`: 对话ID
  - `Content-Type`: `text/event-stream`
- **响应示例**：(SSE流式响应)
```
data: {"msg":"对话成功","code":200,"data":"你"}
data: {"msg":"对话成功","code":200,"data":"好"}
data: {"msg":"对话成功","code":200,"data":"！"}
data: {"msg":"对话成功","code":200,"data":"我"}
```

#### 3.2.3 推荐对话
- **功能名称**：推荐对话
- **请求方式**：POST
- **请求路径**：`/chat/recommendChat`
- **请求体**：
```json
{
  "message": "我想买一部手机",
  "userId": 1,
  "modelId": 1
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "推荐成功",
  "data": {
    "message": "根据您的需求和浏览历史，我为您推荐以下手机...",
    "recommendProducts": [1, 2, 3],
    "chatId": 123
  }
}
```

#### 3.2.4 流式推荐对话
- **功能名称**：流式推荐对话
- **请求方式**：POST
- **请求路径**：`/chat/recommendFluxChat`
- **请求体**：
```json
{
  "message": "我想买一部手机",
  "userId": 1,
  "modelId": 1
}
```
- **响应头**：
  - `X-Conversation-ID`: 对话ID
  - `Content-Type`: `text/event-stream`
- **响应示例**：(SSE流式响应)
```
data: {"msg":"推荐成功","code":200,"data":"根"}
data: {"msg":"推荐成功","code":200,"data":"据"}
data: {"msg":"推荐成功","code":200,"data":"您"}
```

#### 3.2.5 删除对话
- **功能名称**：删除对话
- **请求方式**：DELETE
- **请求路径**：`/chat/deleteById/{id}`
- **路径参数**：`id` (Object) - 对话ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": 1
}
```

#### 3.2.6 分页查询对话记录
- **功能名称**：分页查询对话记录
- **请求方式**：POST
- **请求路径**：`/chat/findChat`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "userId": 1,
    "title": "对话标题"
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "获取成功",
  "data": {
    "total": 50,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": "123",
        "title": "对话标题",
        "createTime": "2024-01-01T10:00:00"
      }
    ]
  }
}
```

#### 3.2.7 保存/修改对话
- **功能名称**：保存/修改对话
- **请求方式**：PUT
- **请求路径**：`/chat/saveChat`
- **请求体**：
```json
{
  "id": "123",
  "title": "新的对话标题",
  "content": "对话内容"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": 123
}
```

#### 3.2.8 游标分页查询对话
- **功能名称**：游标分页查询对话
- **请求方式**：POST
- **请求路径**：`/chat/findCursorChat`
- **请求体**：
```json
{
  "cursor": "string",
  "limit": 10,
  "data": {}
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": "123",
      "title": "对话标题",
      "createTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 3.3 向量检索接口

#### 3.3.1 批量保存商品向量
- **功能名称**：批量保存商品向量
- **请求方式**：POST
- **请求路径**：`/embedding/product/saveBatch`
- **请求体**：
```json
[
  {
    "productId": 1,
    "productName": "商品名称",
    "description": "商品描述",
    "embedding": [0.1, 0.2, 0.3, ...]
  }
]
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": ["vec_id_1", "vec_id_2"]
}
```

#### 3.3.2 删除商品向量
- **功能名称**：删除商品向量
- **请求方式**：DELETE
- **请求路径**：`/embedding/product/deleteByProductId/{productId}`
- **路径参数**：`productId` (Long) - 商品ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

#### 3.3.3 基于用户推荐
- **功能名称**：基于用户推荐
- **请求方式**：POST
- **请求路径**：`/embedding/product/recommendForUser`
- **请求体**：
```json
{
  "data": [
    {
      "productId": 1,
      "embedding": [0.1, 0.2, 0.3, ...]
    }
  ],
  "topK": 10
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "推荐成功",
  "data": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
}
```

#### 3.3.4 关键词搜索
- **功能名称**：关键词搜索
- **请求方式**：GET
- **请求路径**：`/embedding/product/searchKeyWard`
- **请求参数**：
  - `keyWard` (String) - 搜索关键词（必填）
  - `topK` (Integer) - 返回数量（可选，默认10）
- **请求示例**：`/embedding/product/searchKeyWard?keyWard=手机&topK=20`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "搜索成功",
  "data": [1, 2, 3, 4, 5]
}
```

---

## 五、订单服务 (gsy-order-cloud)

订单服务负责订单管理、地址管理等电商核心业务。

### 5.1 订单管理接口

#### 5.1.1 添加订单
- **功能名称**：添加订单
- **请求方式**：POST
- **请求路径**：`/purChaseOrder/save`
- **请求体**：
```json
{
  "orderNo": "ORD202401010001",
  "status": 1,
  "payStatus": 1,
  "receiveStatus": 1,
  "amount": 199.98,
  "payTime": "2024-01-01T10:00:00",
  "orderPlacementTime": "2024-01-01T09:59:00",
  "skuId": 1,
  "payType": "1",
  "deliveryFee": 10.00,
  "delivery": "快递",
  "payAmount": 209.98,
  "remark": "请尽快发货",
  "num": 2,
  "addressId": 1,
  "productId": 100
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "添加成功",
  "data": 123
}
```

#### 5.1.2 修改订单
- **功能名称**：修改订单
- **请求方式**：PUT
- **请求路径**：`/purChaseOrder/updateById`
- **请求体**：
```json
{
  "id": 123,
  "status": 2,
  "payStatus": 2,
  "receiveStatus": 1,
  "payTime": "2024-01-01T10:00:00"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "修改成功",
  "data": 123
}
```

#### 5.1.3 根据用户ID查询订单
- **功能名称**：根据用户ID查询订单
- **请求方式**：GET
- **请求路径**：`/purChaseOrder/findByUserId`
- **请求参数**：
  - `userId` (Long) - 用户ID（必填）
  - `rows` (Integer) - 查询行数（可选，默认10）
- **请求示例**：`/purChaseOrder/findByUserId?userId=1&rows=20`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 123,
      "orderNo": "ORD202401010001",
      "status": 1,
      "amount": 199.98,
      "orderPlacementTime": "2024-01-01T09:59:00"
    }
  ]
}
```

#### 5.1.4 分页查询用户订单
- **功能名称**：分页查询用户订单
- **请求方式**：POST
- **请求路径**：`/purChaseOrder/findByUserIdPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "userId": 1,
    "status": 1
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 50,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 123,
        "orderNo": "ORD202401010001",
        "status": 1,
        "amount": 199.98
      }
    ]
  }
}
```

#### 5.1.5 批量查询用户订单
- **功能名称**：批量查询用户订单
- **请求方式**：POST
- **请求路径**：`/purChaseOrder/findByUserIds`
- **请求体**：
```json
[1, 2, 3]
```
- **请求参数**：
  - `rows` (Integer) - 每个用户查询行数（可选，默认10）
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 123,
      "userId": 1,
      "orderNo": "ORD202401010001",
      "amount": 199.98
    }
  ]
}
```

#### 5.1.6 根据ID查询订单详情
- **功能名称**：根据ID查询订单详情
- **请求方式**：GET
- **请求路径**：`/purChaseOrder/findById/{id}`
- **路径参数**：`id` (Long) - 订单ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 123,
    "orderNo": "ORD202401010001",
    "status": 1,
    "payStatus": 1,
    "receiveStatus": 1,
    "amount": 199.98,
    "payAmount": 209.98,
    "deliveryFee": 10.00,
    "num": 2,
    "productName": "商品名称",
    "skuName": "SKU名称",
    "address": {
      "name": "张三",
      "phone": "13800138000",
      "address": "北京市朝阳区xxx"
    }
  }
}
```

#### 5.1.7 分页查询订单
- **功能名称**：分页查询订单
- **请求方式**：POST
- **请求路径**：`/purChaseOrder/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "orderNo": "ORD202401010001",
    "status": 1,
    "userId": 1
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 123,
        "orderNo": "ORD202401010001",
        "status": 1,
        "amount": 199.98
      }
    ]
  }
}
```

#### 5.1.8 查询最新订单
- **功能名称**：查询最新订单
- **请求方式**：GET
- **请求路径**：`/purChaseOrder/findByRows`
- **请求参数**：
  - `rows` (Integer) - 查询行数（可选，默认10）
- **请求示例**：`/purChaseOrder/findByRows?rows=20`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 123,
      "orderNo": "ORD202401010001",
      "status": 1,
      "amount": 199.98
    }
  ]
}
```

#### 5.1.9 删除订单
- **功能名称**：删除订单
- **请求方式**：DELETE
- **请求路径**：`/purChaseOrder/deletedById/{id}`
- **路径参数**：`id` (Long) - 订单ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": true
}
```

### 5.2 地址管理接口

#### 5.2.1 添加/修改地址
- **功能名称**：添加/修改地址
- **请求方式**：POST
- **请求路径**：`/address/save`
- **请求体**：
```json
{
  "id": 1,
  "address": "北京市朝阳区xxx街道xxx号",
  "phone": "13800138000",
  "name": "张三",
  "description": "公司"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": 1
}
```

#### 5.2.2 删除地址
- **功能名称**：删除地址
- **请求方式**：DELETE
- **请求路径**：`/address/deleteById/{id}`
- **路径参数**：`id` (Long) - 地址ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

#### 5.2.3 根据订单ID查询地址
- **功能名称**：根据订单ID查询地址
- **请求方式**：GET
- **请求路径**：`/address/findByOrderId/{orderId}`
- **路径参数**：`orderId` (Object) - 订单ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "address": "北京市朝阳区xxx街道xxx号",
    "phone": "13800138000",
    "name": "张三",
    "description": "公司"
  }
}
```

#### 5.2.4 批量查询订单地址
- **功能名称**：批量查询订单地址
- **请求方式**：POST
- **请求路径**：`/address/findByOrderIds`
- **请求体**：
```json
[1, 2, 3]
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "orderId": 1,
      "address": "北京市朝阳区xxx",
      "phone": "13800138000",
      "name": "张三"
    }
  ]
}
```

#### 5.2.5 根据用户ID查询地址列表
- **功能名称**：根据用户ID查询地址列表
- **请求方式**：GET
- **请求路径**：`/address/findByUserId/{userId}`
- **路径参数**：`userId` (Object) - 用户ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "address": "北京市朝阳区xxx",
      "phone": "13800138000",
      "name": "张三",
      "description": "公司"
    },
    {
      "id": 2,
      "address": "北京市海淀区xxx",
      "phone": "13800138001",
      "name": "张三",
      "description": "家"
    }
  ]
}
```

#### 5.2.6 根据ID查询地址
- **功能名称**：根据ID查询地址
- **请求方式**：GET
- **请求路径**：`/address/findById/{id}`
- **路径参数**：`id` (Long) - 地址ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "address": "北京市朝阳区xxx",
    "phone": "13800138000",
    "name": "张三",
    "description": "公司"
  }
}
```

#### 5.2.7 分页查询地址
- **功能名称**：分页查询地址
- **请求方式**：POST
- **请求路径**：`/address/findPage`
- **请求体**：
```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {
    "name": "张三",
    "phone": "13800138000"
  }
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 20,
    "pageNum": 1,
    "pageSize": 10,
    "list": [
      {
        "id": 1,
        "address": "北京市朝阳区xxx",
        "phone": "13800138000",
        "name": "张三"
      }
    ]
  }
}
```

---

## 六、上传服务 (gsy-upload-cloud)

上传服务负责文件上传功能，支持图片和各类文件上传。

### 6.1 文件上传接口

#### 6.1.1 上传图片
- **功能名称**：上传图片
- **请求方式**：POST
- **请求路径**：`/upload/image`
- **请求体**：`multipart/form-data` 格式的图片文件
- **Content-Type**：`multipart/form-data`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": [
    "https://example.com/images/2024/01/01/abc123.jpg"
  ]
}
```

#### 6.1.2 上传文件
- **功能名称**：上传文件
- **请求方式**：POST
- **请求路径**：`/upload/upload`
- **请求体**：`multipart/form-data` 格式的文件
- **Content-Type**：`multipart/form-data`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "上传成功",
  "data": "https://example.com/files/2024/01/01/abc123.pdf"
}
```

---

## 七、行为服务 (gsy-behavior-cloud)

行为服务负责用户行为数据收集，包括浏览、点击、收藏、搜索等。

### 4.1 浏览记录接口

#### 4.1.1 添加浏览记录
- **功能名称**：添加浏览记录
- **请求方式**：POST
- **请求路径**：`/browse/save`
- **请求体**：
```json
[
  {
    "userId": 1,
    "productId": 100,
    "browseTime": "2024-01-01T10:00:00",
    "duration": 30
  }
]
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": [1, 2, 3]
}
```

#### 4.1.2 查询浏览记录
- **功能名称**：查询浏览记录
- **请求方式**：GET
- **请求路径**：`/browse/findByRows`
- **请求参数**：
  - `rows` (Integer) - 查询行数（可选）
- **请求示例**：`/browse/findByRows?rows=50`
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "productId": 100,
      "browseTime": "2024-01-01T10:00:00"
    }
  ]
}
```

### 4.2 点击记录接口

#### 4.2.1 添加点击记录
- **功能名称**：添加点击记录
- **请求方式**：POST
- **请求路径**：`/click/save`
- **请求体**：
```json
[
  {
    "userId": 1,
    "productId": 100,
    "clickTime": "2024-01-01T10:00:00"
  }
]
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": [1, 2, 3]
}
```

#### 4.2.2 查询点击记录
- **功能名称**：查询点击记录
- **请求方式**：GET
- **请求路径**：`/click/findByRows`
- **请求参数**：
  - `rows` (Integer) - 查询行数（可选）
- **响应示例**：
```json
{
  "code": 200,
  "msg": "查询成功",
  "data": [...]
}
```

### 4.3 收藏记录接口

#### 4.3.1 添加收藏记录
- **功能名称**：添加收藏记录
- **请求方式**：POST
- **请求路径**：`/collect/save`
- **请求体**：
```json
{
  "userId": 1,
  "productId": 100,
  "collectTime": "2024-01-01T10:00:00"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": 1
}
```

#### 4.3.2 删除收藏记录
- **功能名称**：删除收藏记录
- **请求方式**：DELETE
- **请求路径**：`/collect/deleteById/{id}`
- **路径参数**：`id` (Long) - 收藏记录ID
- **响应示例**：
```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

### 4.4 搜索记录接口

#### 4.4.1 添加搜索记录
- **功能名称**：添加搜索记录
- **请求方式**：POST
- **请求路径**：`/search/save`
- **请求体**：
```json
{
  "userId": 1,
  "keyword": "搜索关键词",
  "searchTime": "2024-01-01T10:00:00"
}
```
- **响应示例**：
```json
{
  "code": 200,
  "msg": "保存成功",
  "data": 1
}
```

---

## 八、通用说明

### 5.1 统一响应格式

所有接口统一使用以下响应格式：

```json
{
  "code": 200,
  "msg": "成功消息",
  "data": {}
}
```

### 5.2 响应码说明

| 响应码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 禁止访问（如登录失败、权限不足） |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 5.3 分页请求格式

```json
{
  "pageNum": 1,
  "pageSize": 10,
  "data": {}
}
```

### 5.4 分页响应格式

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "list": []
  }
}
```

### 5.5 游标分页请求格式

```json
{
  "cursor": "string",
  "limit": 10,
  "data": {}
}
```

### 5.6 认证说明

除白名单接口外，所有接口需要在请求头中携带Token：

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 5.7 公开接口（无需认证）

以下接口无需认证即可访问：
- `/sys-api/signInUp/signIn` - 登录
- `/sys-api/signInUp/signUp` - 注册
- `/sys-api/refresh` - 刷新Token
- `/sys-api/verify` - 验证
- `/sys-api/reset/forget` - 忘记密码
- `/goods-api/product/carousal/findByType/` - 根据类型查询轮播图

---

## 附录：服务端口说明

| 服务名称 | 服务模块 | 说明 |
|---------|---------|------|
| 系统服务 | gsy-system-cloud | 用户、权限、租户管理 |
| 商品服务 | gsy-goods-cloud | 商品、SKU、分类、标签、轮播图、仓库管理 |
| AI服务 | gsy-ai-cloud | 大模型、对话、向量检索 |
| 订单服务 | gsy-order-cloud | 订单、地址管理 |
| 上传服务 | gsy-upload-cloud | 文件上传 |
| 行为服务 | gsy-behavior-cloud | 用户行为数据收集 |
| 网关服务 | gsy-gateway-cloud | API网关、路由转发 |

---

**文档版本**: v1.0  
**更新时间**: 2026-04-07  
**维护者**: AI推荐云平台团队
