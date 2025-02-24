# SimplePapiAPI
## 简介  
一个简单的将PlaceholderAPI的变量构建为一个API，允许外部通过GET请求的方式调用的Paper插件。
## 安装
1. 下载`SimplePapiAPI-1.0.jar`并移动到你的服务器`/plugins/`目录下。
2. 重启服务器
3. 在服务器本地访问`http://localhost:4747/`，若成功安装，它应该会显示`Missing parameters`
## 调用
SimplePapiAPI构建的是HTTP服务器，其支持GET方法调用。  
请求URL：http://localhost:4747  
请求方式：GET  
| 参数名 | 必选 | 类型 | 说明 | 示例 |
|--------|------|------|------|------|
| target | 是 | string | 使用指定的target查询变量 | "testplayer" |
| variable | 是 | string | 要查询的变量列表 | ["%player_health%","%player_exp%"] |  

**请注意，在请求前您必须将请求参数转义为URL编码后才可使用，否则将会报错 `400 Bad Request URISyntaxException thrown`**  
请求示例：
http://localhost:4747?target=fljlus&variable=["%player_health%","%player_exp%"]
返回示例:
```json
{"%player_health%": "20", "%player_exp%": "120"}
```
