# 接口文档

仓储管理系统后端接口说明。

---

## 通用约定

**Base URL**：`http://localhost:8081`

**认证**：除 `/login` 外，所有接口都要在请求头带上登录返回的 token。

```
token: eyJhbGciOiJIUzI1NiJ9....
```

**统一返回结构**

```json
{
  "code": 1,
  "msg": "操作成功",
  "date": null
}
```

| 字段 | 说明 |
| --- | --- |
| `code` | `1` 成功，`0` 失败 |
| `msg` | 提示信息，失败时是具体原因 |
| `date` | 数据。列表接口返回 `{ "totals": 总数, "rows": [...] }` |

**状态码约定**

| 场景 | HTTP 状态码 | 响应体 |
| --- | --- | --- |
| 未登录 / token 缺失或失效 | `401` | `{"code":0,"msg":"NOT_login","date":null}` |
| 请求过于频繁 | `429` | `{"code":0,"msg":"请求过于频繁，20 秒内最多 20 次，请稍后再试","date":null}` |
| 业务失败（如商品已存在） | `200` | `{"code":0,"msg":"商品已经存在！","date":null}` |
| 参数校验失败 | `200` | `{"code":0,"msg":"用户名不可以为空！","date":null}` |

> 业务错误沿用了 HTTP 200 + `code=0` 的约定，前端以 `code` 判断成败。

**限流**

- 按客户端 IP 计数，`20` 秒内最多 `20` 次请求
- 超限返回 `429`，响应头带 `Retry-After`（单位：秒）
- `/login` 不在限流范围内
- Redis 不可用时自动降级放行（fail-open）

**分页参数命名**

| 接口 | 每页条数参数名 | 默认值 |
| --- | --- | --- |
| `/goods/selectgoods` | `pagesize` | 5 |
| `/stock/selectstock` | `pagesize` | 5 |
| `/ware/selectware` | `pageSize` | 5 |
| `/stockrecord/selectrecord` | `pageSize` | 5 |
| `/select`（人员） | `pageSize` | 5 |

> 历史原因大小写不一致，调用时请按上表传参，传错会退回默认值。

---

## 一、登录

### 账号密码登录

```
GET /login?username={username}&password={password}
```

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| `username` | query | string | 是 | 用户名 |
| `password` | query | string | 是 | 明文密码，服务端用 BCrypt 与库中哈希比对 |

**成功响应**：直接返回 JWT 字符串，**不套 `Result`**

```
eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwicm9sZSI6NCwiZXhwIjoxNzYyNjE4ODAwfQ.xxxx
```

**失败响应**

```json
{ "code": 0, "msg": "账号或密码错误", "date": null }
```

> - token 默认 30 分钟过期（`jwt.expire-minutes` 可配），过期后需重新登录
> - 用户不存在与密码错误返回同一句提示，避免被枚举用户名
> - 明文密码以 query 参数传递，仅适用于本地演示；正式环境应改为 POST + JSON 请求体

---

## 二、商品

### 2.1 新增商品

```
POST /goods/addgoods
Content-Type: application/json
```

```json
{
  "goodsName": "农夫山泉 550ml",
  "goodsCode": "SKU-1001",
  "unit": "箱",
  "remark": "常备饮用水"
}
```

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `goodsName` | string | 是 | 商品名称 |
| `goodsCode` | string | 是 | 商品编码，重复会报错 |
| `unit` | string | 否 | 单位 |
| `remark` | string | 否 | 备注 |

**成功**：`{"code":1,"msg":"操作成功","date":null}`
**失败**：`{"code":0,"msg":"商品已经存在！","date":null}`

### 2.2 分页查询商品

```
GET /goods/selectgoods?page=1&pagesize=5
```

| 参数 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `page` | int | 1 | 页码，从 1 开始 |
| `pagesize` | int | 5 | 每页条数 |

```json
{
  "code": 1,
  "msg": "操作成功",
  "date": {
    "totals": 8,
    "rows": [
      {
        "goodsId": 1,
        "goodsName": "农夫山泉 550ml",
        "goodsCode": "SKU-1001",
        "unit": "箱",
        "remark": "常备饮用水",
        "updateTime": "2026-11-06T20:21:40"
      }
    ]
  }
}
```

### 2.3 修改商品

```
PUT /goods/updategoods
```

```json
{ "goodsId": 1, "goodsName": "农夫山泉 550ml（新）", "goodsCode": "SKU-1001", "unit": "箱" }
```

> 使用 `version` 乐观锁，并发修改时只有一个成功，失败返回 `已被修改！`

### 2.4 删除商品（逻辑删除）

```
DELETE /goods/deletegoods?goodsId=1
```

> 只把 `is_delete` 置为 1，数据仍在库中。

### 2.5 模糊查询

```
GET /goods/likeselect?goodsName=水&createTime=2026-11-06 20:21:40
```

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `goodsName` | string | 否 | 商品名称，模糊匹配 |
| `createTime` | string | 否 | 创建时间，格式 `yyyy-MM-dd HH:mm:ss` |

**响应**：直接返回商品数组（不分页）

```json
{ "code": 1, "msg": "操作成功", "date": [ { "goodsId": 1, "goodsName": "农夫山泉 550ml" } ] }
```

---

## 三、仓库

### 3.1 新增仓库

```
POST /ware/addware
```

```json
{ "warehouseName": "广州中心仓", "address": "广州市白云区太和镇", "status": 1 }
```

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `warehouseName` | string | 是 | 仓库名称，重复会报错 |
| `address` | string | 否 | 地址 |
| `status` | int | 否 | 1 正常 / 0 停用 |

### 3.2 分页查询仓库

```
GET /ware/selectware?page=1&pageSize=5
```

返回结构同商品分页：`{ totals, rows }`。

### 3.3 修改仓库

```
PUT /ware/updateware
```

```json
{ "warehouseId": 1, "warehouseName": "广州中心仓（新）", "address": "广州市白云区", "status": 1 }
```

### 3.4 删除仓库（逻辑删除）

```
DELETE /ware/deleteware?id=1
```

---

## 四、库存

### 4.1 建立库存（仓库 × 商品）

```
POST /stock/addstock
```

```json
{ "warehouseId": 1, "goodsId": 1, "stockNum": 320, "safeNum": 3000 }
```

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| `warehouseId` | int | 是 | 仓库 ID |
| `goodsId` | int | 是 | 商品 ID |
| `stockNum` | int | 是 | 初始库存数量 |
| `safeNum` | int | 是 | 安全库存上限，`stockNum` 不能超过它 |

**业务校验**

| 情况 | 返回消息 |
| --- | --- |
| 商品不存在 | `该商品不存在！` |
| 仓库不存在或已停用 | `该仓库不存在！` |
| `stockNum > safeNum` | `超出安全值！` |
| 同一仓库 + 商品已存在 | `该库存已经存在！不可以在建立` |
| 角色为 1（只读） | `权限不足！` |

### 4.2 增减库存数量

```
PUT /stock/addnum?id=1&num=10
```

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| `id` | int | 库存记录 ID |
| `num` | int | 正数=入库，负数=出库 |

**业务规则**

- 累加后不能超过 `safeNum`，否则返回 `超出安全值，请到其他仓库存！`
- 累加后不能小于 0，否则返回 `该仓库库存不足！`
- 更新带 `version` 乐观锁，并发失败返回 `请稍后重式！`
- **每次成功都会写入一条流水**：`operateType` 1=入库、2=出库，`operatorId` 取自 token 中的用户 id

### 4.3 修改库存信息

```
PUT /stock/updatestock
```

```json
{ "stockId": 1, "warehouseId": 1, "goodsId": 1, "stockNum": 320, "safeNum": 3000 }
```

### 4.4 分页查询库存

```
GET /stock/selectstock?page=1&pagesize=5
```

联表返回仓库名、商品名、数量、安全值：

```json
{
  "code": 1,
  "msg": "操作成功",
  "date": {
    "totals": 8,
    "rows": [
      { "stockId": 3, "warehouseName": "广州中心仓", "goodsName": "农夫山泉 550ml", "stockNum": 320, "safeNum": 3000 }
    ]
  }
}
```

> 该接口启用了 Redis 旁路缓存，key 形如 `cache:stock:1:10`，TTL 60 秒。

---

## 五、出入库流水

### 5.1 分页查询流水

```
GET /stockrecord/selectrecord?page=1&pageSize=5
```

| 参数 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| `page` | int | 1 | 页码 |
| `pageSize` | int | 5 | 每页条数 |

返回字段：`recordId`、`warehouseId`、`goodsId`、`operateType`、`num`、`operatorId`、`createTime`。

> `role=1` 的账号无权查看流水，返回 `权限不足！`

---

## 六、人员管理

### 6.1 新增账号

```
POST /addrole
```

```json
{ "username": "zhangsan", "password": "123456", "role": 2 }
```

| 字段 | 校验 |
| --- | --- |
| `username` | 不能为空 |
| `password` | 不能为空，入库前 BCrypt 加密 |
| `role` | 1 只读 / 2 可写 / 3 管理员 / 4 超级管理员 |

### 6.2 分页查询账号

```
GET /select?page=1&pageSize=5
```

返回 `id`、`username`、`role`，不返回密码。

### 6.3 修改账号

```
PUT /update
```

```json
{ "id": 2, "username": "zhangsan", "password": "新密码", "role": 3 }
```

### 6.4 删除账号（逻辑删除）

```
DELETE /delete
```

请求体：

```json
{ "id": 2 }
```

### 6.5 按用户名模糊查询

```
GET /like?username=张
```

---

## 附：快速验证

```bash
# 1. 登录拿 token
curl "http://localhost:8081/login?username=admin&password=123456"

# 2. 带 token 查询库存
curl -H "token: <上一步的返回值>" "http://localhost:8081/stock/selectstock?page=1&pagesize=10"

# 3. 连续请求 21 次，第 21 次应返回 429
for i in $(seq 1 21); do
  curl -s -o /dev/null -w "%{http_code}\n" -H "token: <token>" \
       "http://localhost:8081/stock/selectstock?page=1&pagesize=10"
done
```

默认账号（执行 `sql/demo-data.sql` 后可用）：

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| `admin` | `123456` | 4（超级管理员） |
| `operator` | `123456` | 2（可写） |
