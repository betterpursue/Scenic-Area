# API 使用文档

## 全局说明

*   **根路径 (Base URL):** `http://localhost:8080`
*   **认证 (Authentication):** 所有需要认证的接口都必须在 HTTP 请求头 (Header) 中包含 `X-Merchant-Id` 字段，值为商户的 ID。

    *   **示例:** `X-Merchant-Id: 1`
    *   *备注: 当前为了方便测试，认证机制被简化。在生产环境中，应替换为更安全的认证方式（如 JWT）。*

---

## 1. 礼品码核销

核销一个礼品码。

*   **URL:** `/api/v1/redeem`
*   **Method:** `POST`
*   **Headers:**
    *   `Content-Type: application/json; charset=utf-8`
    *   `X-Merchant-Id: {merchant_id}`
*   **Request Body:**

    ```json
    {
      "code": "TESTCODE123"
    }
    ```

### 响应

*   **成功 (200 OK):**

    ```json
    {
      "code": 200,
      "message": "核销成功",
      "data": null
    }
    ```

*   **失败 (4xx/5xx):**

    ```json
    {
      "code": 400,
      "message": "兑换码不存在", // 失败原因
      "data": null
    }
    ```
    *常见的失败原因包括：兑换码不存在、已被核销、已过期、不属于当前商家等。*

---

## 2. 查询核销记录

查询当前商户的核销历史记录。

*   **URL:** `/api/v1/redeem/records`
*   **Method:** `GET`
*   **Headers:**
    *   `X-Merchant-Id: {merchant_id}`
*   **Query Parameters:**
    *   `startDate` (可选, `string`): 查询开始时间, 格式为 `YYYY-MM-DDTHH:mm:ss` (例如: `2025-01-01T00:00:00`)。
    *   `endDate` (可选, `string`): 查询结束时间, 格式同上。
    *   `page` (可选, `integer`, 默认: `1`): 页码。
    *   `size` (可选, `integer`, 默认: `10`): 每页数量。

### 响应

*   **成功 (200 OK):**

    ```json
    {
      "code": 200,
      "message": "操作成功",
      "data": [
        {
          "code": "TESTCODE123",
          "goodsName": "Test Goods",
          "visitorId": "VI****DE",
          "redeemTime": "2025-12-15T12:52:21.929065"
        }
      ]
    }
    ```

---

## 3. 查询核销数据统计

查询当前商户在指定时间周期内的核销数据统计。

*   **URL:** `/api/v1/redeem/statistics`
*   **Method:** `GET`
*   **Headers:**
    *   `X-Merchant-Id: {merchant_id}`
*   **Query Parameters:**
    *   `period` (可选, `string`, 默认: `WEEK`): 统计周期。可选值为 `DAY`, `WEEK`, `MONTH`。

### 响应

*   **成功 (200 OK):**

    ```json
    {
      "code": 200,
      "message": "操作成功",
      "data": {
        "totalRedeemed": 1,
        "totalGiftTypes": 1,
        "dailyTrends": [
          {
            "date": "2025-12-15",
            "count": 1
          }
        ],
        "weeklyTrends": [
          {
            "week": "2025-51",
            "count": 1
          }
        ],
        "monthlyTrends": [
          {
            "month": "2025-12",
            "count": 1
          }
        ]
      }
    }
    ```
