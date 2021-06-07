## HTTP面试整理

### 一. HTTP基础

+ 什么是HTTP，HTTP结构

```
超文本传输协议，是一个基于请求与响应的应用层传输协议。
```

```
请求报文结构:
1. 请求行:  请求方法 + 请求地址 + HTTP版本
2. 请求头:  key: val --> cookie,accetp
3. 请求体:  

GET /wxisme HTTP/1.1  
Host: www.cnblogs.com 
User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.0; zh-CN; rv:1.8.1) Gecko/20061010 Firefox/2.0  
Accept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5  
Accept-Language: en-us,zh-cn;q=0.7,zh;q=0.3  
Accept-Encoding: gzip,deflate  
Accept-Charset: gb2312,utf-8;q=0.7,*;q=0.7  
Keep-Alive: 300  
Proxy-Connection: keep-alive  
Cookie: ASP.NET_SessionId=ey5drq45lsomio55hoydzc45
Cache-Control: max-age=0
```

```
响应报文结构:
1. 状态行: HTTP版本 + 转态码 + 状态描述
2. 响应头: key: val --> contentType
3. 响应体:

HTTP/1.1 200 OK
Date: Tue, 12 Jul 2016 21:36:12 GMT
Content-Length: 563
Content-Type: text/html

<html>
    <body>
    Hello http!
    </body>
</html>
```

+ HTTP的方法，get、post的区别

```
1. get: 请求数据
2. post: 提交数据
3. put: 提交数据
4. delete: 删除数据
5. options: 获取指出的方法
6. head: 不返回数据，只需要头部
7. connect: 使用隧道传输

get/post的区别:
1. 最大的区别是语义，get是请求数据，post是提交数据。
2. get会将请求内容拼接在url中，post会放在body中。
3. get由于拼接在url的举动，长度收到url的限制，body的容量无限制。
4. 私密请求推荐post，虽然两者都不安全。
5. get是幂等的，post不支持幂等。
```

+ HTTP状态码

```
1xx: 请求成功，正在处理
2xx: 访问成功
3xx: 重定向 304 not modify: 浏览器缓存,未修改
4xx: 客户端错误 403 禁止 404 not found
5xx: 服务器错误 500
```

+ HTTP和TCP heepalive机制

```
TCP的keepalive机制是用来确认对端是否存活。探测机制。
如果对端存活，发送回应，继续维持连接，重新计时
如果对端挂掉或者网络故障，超过次数主动关闭链接
如果对端挂掉之后重启，对端会发送fin，重新建立链接。
HTTP的keepalive机制是用来保证连接不会一次请求响应就被关闭，维护长连接。
http默认是一次请求响应就关闭链接。
开启keepalive之后，可以使用心跳包来维护长连接。
```

+ 简述 HTTP 短链接与长链接的区别

```
HTTP/1.0默认短连接，一次连接只支持一次请求与响应，响应结束关闭链接。
HTTP/1.1之后，默认长链接，Connection: keep-alive。 请求响应结束之后，不会马上关闭链接，之后的请求回复用这个连接。
HTTP的长连接和短连接是基于TCP的长连接和短连接实现的。
实际上就是，HTTP的短连接就是一次请求响应结束之后，client会主动发起关闭链接请求。
HTTP的长连接，就是利用TCP的keepalive机制保活，不主动关闭链接。
```

+ HTTP 1.0/1.1/2.0的区别

```
1.0 -> 1.1:
 + 默认支持长连接，从协议上不会发起关闭链接请求。
 + 增加新的状态码
 + 增加新的缓存处理
 + 优化带宽和网络连接 : range请求部分对象
 
1.1 -> 2.0:
 + 首部压缩
 + 服务器主动推送: 一次请求，多个推送
 + 二进制分帧:
 + 多路复用:
```

+ HTTP与HTTPS的区别

```
HTTPS = HTTP + SSL
port： 90 -> 443
client 			server
		<---    数字证书(数字签名+非对称公钥)
验证签名
生成对称私钥
公钥加密私钥
        ---->
   				非对称私钥解密 获得对称私钥
对称私钥加密内容
		---->
				对称私钥解密
```

+ cookie和session的区别

```
HTTP是无状态的，每一次请求与响应都是无关的。
但是会话跟踪是很有必要的，保证一个用户的请求属于一个会话。解决，携带一个表示(通行证)记录用户的状态。
cookie是由server生成，随响应下发给client，下次请求client将cookie附带在请求头中。
session是server生成sessionId+session，sessionId下发给cliet，server保存对应的session，相对安全不容易被篡改，但是增加server压力。
```

