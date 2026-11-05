# 接口文档

仓储管理系统后端接口说明。

## 通用约定

**Base URL**：`http://localhost:8081`

**认证**：除 `/login` 外，所有接口都要在请求头带上登录返回的 token：

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
| code | `1` 成功，`0` 失败 |
| msg | 提示信息，失败时是具体原因 |
| date | 数据，列表接口返回 `{ totals, rows }` |

**错误约定**

| 场景 | HTTP 状态码 | 返回 |
| --- | --- | --- |
| 未登录 / token 失效 | 401 | `{"code":0,"msg":"NOT_login"}` |
| 请求过于频繁 | 429 | `{"code":0,"msg":"请求过于频繁，20 秒内最多 20 次，请稍后再试"}` |
| 业务失败（如商品已存在） | 200 | `{"code":0,"msg":"商品已经存在！"}` |

> 业务错误沿用 HTTP 200 + `code=0`，前端以 `code` 为准。

**限流**：单 IP 20 秒内最多 20 次请求，超限返回 429，响应头带 `Retry-After`（单位：秒）。

---

## 一、登录

### 账号密码登录

```
GET /login
```

| 参数 | 位置 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- | --- |
| username | query | string | 是 | 用户名 |
| password | query | string | 是 | 明文密码，服务端用 BCrypt 比对 |

**成功响应**：直接返回 token 字符串（注意：此接口不套 `Result`）

```
eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MSwicm9sZSI6MiwiZXhwIjoxNzYyMDk4MjAwfQ.xxxx
```

**失败响应**

```json
{ "code": 0, "msg": "账号或密码错误", "date": null }
```

> token 默认 30 分钟过期，过期后重新登录。

---

## 二、商品

### 2.1 新增商品

```
POST /goods/addgoods
Content-Type: application/json
```

```json
{
  "goodsName": "矿泉水",
  "goodsCode": "G-1001",
  "unit": "箱",
  "remark": "备注可选"
}
```

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| goodsName | string | 是 | 商品名称 |
| goodsCode | string | 是 | 商品编码，全局唯一，重复会报错 |
| unit | string | 否 | 单位 |
| remark | string | 否 | 备注 |

**响应**：`{"code":1,"msg":"操作成功","date":null}`

业务失败：`{"code":0,"msg":"商品已经存在！"}`

### 2.2 分页查询商品

```
GET /goods/selectgoods?page=1&pagesize=5
```

| 参数 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| page | int | 1 | 页码，从 1 开始 |
| pagesize | int | 5 | 每页条数 |

```json
{
  "code": 1,
  "msg": "操作成功",
  "date": {
    "totals": 12,
    "rows": [
      { "goodsId": 1, "goodsName": "矿泉水", "goodsCode": "G-1001", "unit": "箱", "updateTime": "2026-10-31T10:00:00" }
    ]
  }
}
```

### 2.3 修改商品

```
PUT /goods/updategoods
```

```json
{ "goodsId": 1, "goodsName": "矿泉水（改）", "goodsCode": "G-1001", "unit": "箱" }
```

> 内部使用 `version` 乐观锁，并发修改时只有一个能成功，失败的返回 `已被修改！`。

### 2.4 删除商品（逻辑删除）

```
DELETE /goods/deletedoods?goodsId=1
```

> 只把 `is_delete` 置为 1，数据仍在库中。

### 2.5 模糊查询

```
GET /goods/likeselect?goodsName=水
```

| 参数 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| goodsName | string | 否 | 商品名称，模糊匹配 |
| createTime | string | 否 | 创建时间，格式 `yyyy-MM-dd HH:mm:ss` |

---

## 三、仓库

### 3.1 新增仓库

```
POST /ware/addware
```

```json
{ "warehouseName": "一号仓", "address": "上海市", "status": 1 }
```

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| warehouseName | string | 是 | 仓库名称，重复会报错 |
| address | string | 否 | 地址 |
| status | int | 否 | 1 正常 / 0 停用 |

### 3.2 分页查询仓库

```
GET /ware/selectware?page=1&pageSize=5
```

返回同商品分页结构：`{ totals, rows }`。

### 3.3 修改仓库

```
PUT /ware/updateware
```

```json
{ "warehouseId": 1, "warehouseName": "一号仓（改）", "address": "上海", "status": 1 }
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
{ "warehouseId": 1, "goodsId": 1, "stockNum": 100, "safeNum": 500 }
```

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| warehouseId | int | 是 | 仓库 ID |
| goodsId | int | 是 | 商品 ID |
| stockNum | int | 是 | 初始库存数量 |
| safeNum | int | 是 | 安全库存上限，`stockNum` 不能超过它 |

业务校验：商品必须存在、仓库必须存在且未停用、同一「仓库 × 商品」不能重复建库存。

### 4.2 增减库存数量

```
PUT /stock/addnum?id=1&num=10
```

| 参数 | 类型 | 说明 |
| --- | --- | --- |
| id | int | 库存记录 ID |
| num | int | 正数=入库，负数=出库 |

- 累加后不能超过 `safeNum`，否则报 `超出安全值`
- 累加后不能小于 0，否则报 `该仓库库存不足`
- **每次变动都会写一条流水**，`operateType` 1=入库、2=出库

### 4.3 修改库存信息

```
PUT /stock/updatestock
```

```json
{ "stockId": 1, "warehouseId": 1, "goodsId": 1, "stockNum": 100, "safeNum": 500 }
```

### 4.4 分页查询库存

```
GET /stock/selectstock?page=1&pagesize=5
```

联表返回「仓库名 + 商品名 + 数量 + 安全值」。

---

## 五、出入库流水

### 5.1 分页查询流水

```
GET /stockrecord/selectrecord?page=1&pageSize=5
```

| 参数 | 类型 | 默认 | 说明 |
| --- | --- | --- | --- |
| page | int | 1 | 页码 |
| pageSize | int | 5 | 每页条数 |

返回字段：流水 ID、商品、仓库、变动数量、操作类型、操作人、时间。

> `role=1` 的账号无权查看流水。

---

## 六、人员管理

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/addrole` | 新增账号 |
| GET | `/select?page=1&pageSize=5` | 分页查询账号 |
| PUT | `/update` | 修改账号（用户名、密码、角色） |
| DELETE | `/delete` | 逻辑删除账号（请求体传 `id`） |
| GET | `/like?username=朱` | 按用户名模糊查询 |

新增账号请求体：

```json
{ "username": "zhangsan", "password": "123456", "role": 2 }
```

| 字段 | 校验 |
| --- | --- |
| username | 不能为空 |
| password | 不能为空，入库前 BCrypt 加密 |

---

## 附：测试建议

```bash
# 1. 登录拿 token
curl "http://localhost:8081/login?username=admin&password=123456"

# 2. 带上 token 访问
curl -H "token: <上一步的返回值>" "http://localhost:8081/goods/selectgoods?page=1&pagesize=5"

# 3. 连续打 21 次，第 21 次应返回 429
for i in $(seq 1 21); do curl -s -o /dev/null -w "%{http_code}\n" -H "token: <token>" "http://localhost:8081/goods/selectgoods"; done
```
