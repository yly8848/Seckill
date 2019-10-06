# Seckill

一个基于 Servlet 4.0 的简易商品后台秒杀系统

## 功能简介

- 对商品的抢购进行反馈及数据库相关的记录

## 相关依赖

<div align="left">
    <img src="img\3.png">
</div>

-  `MySQL`  + `mysql-connector.jar`  + `c3p0.jar` + `DbUtil.jar`
- `Redis` + `jedis.jar` + `commons-pool2.jar`
- `RabbitMQ` + `amqp-clienct.jar` 



## 参考

- 架构参考: https://blog.csdn.net/Jorocco/article/details/82182500
- RabbitMQ参考:
  + https://blog.csdn.net/hellozpc/article/details/81436980
  + https://www.rabbitmq.com/getstarted.html
- Redis 参考:
  + https://www.runoob.com/redis/redis-java.html
  + https://blog.csdn.net/u013256816/article/details/51125842/



## 实现过程

### 1) 普通方式

![](img\4.png)

![基本架构图](img\5.png)

后台获取`good_id`和`phone`后，直接对数据库进行操作：是否重复购买、库存余量的判断

其修改库存的SQL语句为：

`update good set number=number-1 where good_id=? and number>0 and ...`

由MySQL对库存`number`进行判断，数据库进行 `update` 操作时会加锁，先执行**库存减1**的操作，再根据该SQL语句是否执行成功进行 **购买记录**的插入；**这样子的话，后台无需进行同步操作，也无需担心商品是否多卖或者少买的情况**

![1](img\2.png)

经过 Apache ab 的多次并发量测，同步确实没有问题。但是，**并发访问的数量一增大，数据库就会被击垮！**

![2](img\1.png)



### 2) 添加上 Redis 缓存

![Redis架构](img\6.png)

把商品的库存放到 Redis 中，每次抢购请求都对库存进行减1操作，如果当前库存大于等于0那么代表秒杀成功。其中Redis的原子操作保证的同步的安全性。同时也将当前时间可以售卖的商品添加进缓存，某商品是否允许售卖主要看缓存是否存在。

![](img\7.png)

通过ab的测试，（重复购买就不判断了，ab填参数的时候不会弄动态的phone~）很明显并发量达到了1000以上，瓶颈主要出现在对数据库的操作上。可以预见：一旦库存量很大，达到10000+的级别，按照上面的写法，即使用了Redis，高并发之下数据库仍然会挂掉！



### 3) 添加上消息队列

![](img\8.png)

一旦用户抢到了商品，后台马上反馈信息，抢购信息的记录交由消息队列异步地去完成，大大缩减了前端等待消息的时间，数据库的写入速度也能得到控制。

![](img\9.png)

继续进行ab的并发测试，通过观察RabbitMQ的后台发现其抢购成功的峰值到达了 2k+ 每秒的访问量，其瓶颈可能出现自Tomcat的配置或者测试机器的性能