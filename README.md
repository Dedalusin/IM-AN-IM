# YEAH I'M AN IM
A basic IM system.
------
### 大致分为以下模块
- common api约定和相关util
- webGate 客户端短连接登陆验证、获取bestNode
- server netty服务器，负责与客户端通信和消息转发
- client 客户端
------- 
其余使用redis作为session存储，zookeeper实现分布式命名服务以及在线统计，消息记录可结合消息队列异步推送至离线消息库
