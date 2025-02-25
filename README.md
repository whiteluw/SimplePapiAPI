# SimplePapiAPI
## 简介  
一个简单的将PlaceholderAPI的变量构建为一个API，允许外部通过GET请求的方式调用的Paper插件。
插件特性：
* 简单易用：通过标准的 HTTP GET 请求即可获取数据
* 灵活性高：支持批量查询多个变量
* 通用性强：可以与任何支持 HTTP 请求的编程语言或平台集成
* 扩展性强：可以查询绝大多数 PlaceholderAPI 支持的变量
## 使用场景&工作原理  
* 使用其他语言外部获取玩家数据或服务器负载数据
* 玩家数据统计网站
* 供玩家开发者作为接口调用
* 结合DiscordBot实现查询玩家数据
* 定期收集玩家数据进行分析
* ...

注：该插件仅为实现上述功能便于开发的前置插件，本身并不包含上述功能

SimplePapiAPI内置了一个HTTP服务器，其将会响应外部的GET请求。
收到请求并处理参数后，通过PlaceholderAPI查询每个变量。最后通过JSON的格式返回查询结果，例如：
```json
{"%player_health%": "20", "%player_exp%": "120"}
```

## 安装
0. 服务器内必须先安装`PlaceholderAPI`插件。
1. 下载`SimplePapiAPI-1.0.jar`并移动到你的服务器`/plugins/`目录下。
2. 重启服务器
3. 在服务器本地访问`http://localhost:4747/`，若成功安装，它应该会显示`Missing parameters`
## 调用
SimplePapiAPI构建的是HTTP服务器，其支持GET方法调用。    
请求URL：`http://localhost:4747`  
请求方式：GET  
| 参数名 | 必选 | 类型 | 说明 | 示例 |
|--------|------|------|------|------|
| target | 是 | string | 使用指定的target查询变量 | "testplayer" |
| variable | 是 | string | 要查询的变量列表 | ["%player_health%","%player_exp%"] |  

**请注意，在请求前您必须将请求参数转义为URL编码后才可使用，否则将会报错 `400 Bad Request URISyntaxException thrown`**  

#### 请求示例  
`http://localhost:4747?target=whitelu&variable=["%player_health%","%player_exp%"]`  

转义为  

`http://localhost:4747?target=whitelu&variable=%5B%25player_health%25%2C%25player_exp%25%5D`  
#### 返回示例  
```json
{"%player_health%": "20", "%player_exp%": "120"}
```

#### 使用Python调用
```
pip install requests
```
```python
import requests

def get_papi_variables(target, variables):
    """
    获取PlaceholderAPI变量值
    :param target: 目标玩家名称
    :param variables: 变量列表
    :return: 变量值字典
    """
    base_url = "http://localhost:4747/"
    variable_list = f"[{','.join(variables)}]"
    params = {'target': target,'variable': variable_list}
    try:
        response = requests.get(base_url, params=params)
        if response.status_code == 200:
            return response.json()
        else:
            print(f"Error: Status code {response.status_code}")
            print(f"Response: {response.text}")
            return None
    except Exception as e:
        print(f"Request failed: {str(e)}")
        return None

target = "testplayer"
variables = ["%ez-statistic_DAMAGE_DEALT%","%ez-statistic_DAMAGE_TAKEN%"]
result = get_papi_variables(target, variables)
print(result)
```

## 其他事项
#### 兼容性
该插件是在Paper API 1.20版本上构建的，理论支持Paper，Spigot，Bukkit的1.20.x-1.21.x。
对于PlaceholderAPI，至少需要 2.10.0 及以上版本
#### 安全性
此插件添加后，意味着任何有权访问4747端口的人将可随意调取你的服务器上的所有PlaceholderAPI数据。  
因此，若无特殊外网访问需求，**请不要在防火墙放行4747端口**。若必须使用，建议仅放行给您信任的IP地址。  
在后续的插件开发中，我们将添加更多安全功能，详见TODO。
#### TODO  
 - [x] 控制台输出请求日志
 - [ ] 支持修改端口
 - [ ] 添加请求速率限制
 - [ ] 优化请求格式
 - [ ] 添加请求变量的黑白名单

## 附录：URL编码符号对照表：
| 源符号 | 编码后 |
|------|------|
| [ | %5B |
| ] | %5D |
| % | %25 | 
| , | %2C |
