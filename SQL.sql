/*数据库初始化脚本*/

create database seckill;
use seckill;

create table good(
    `good_id` bigint not null auto_increment comment '商品ID',
    `good_name` char(200) not null comment '商品名',
    `number` int not null comment '库存',
    `start_time` datetime not null comment '开始时间',
    `end_time` datetime not null comment '结束时间',
    `create_time` timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
    primary key (good_id),
    key idx_start_time(start_time),
    key idx_end_time(end_time),
    key idx_create_time(create_time)
)ENGINE=InnoDB auto_increment=1000 default charset=utf8 comment='秒杀商品库存表';

-- 插入初始化数据
insert into
    good(good_name,number,start_time,end_time)
values
    ('100元秒杀小米9',100,'2019-10-01 00:00:00','2019-10-02 00:00:00'),
    ('300元秒杀iPhoneX',150,'2019-9-30 00:00:00','2019-10-01 00:00:00'),
    ('500元秒杀大冰箱',300,'2019-10-02 00:00:00','2019-10-04 00:00:00'),
    ('1元秒杀小米手环4',50,'2019-10-01 00:00:00','2019-10-02 00:00:00');

-- 秒杀成功明细表

create table success_kill(
    `good_id` bigint not null comment '商品ID',
    `user_phone` bigint not null comment '用户手机号',
    `state` tinyint not null default -1 comment '状态标识: -1:无效, 0:成功, 1:已付款',
    `create_time` timestamp not null default CURRENT_TIMESTAMP comment '创建时间',
    primary KEY (good_id,user_phone),
    key idx_create_time(create_time)
)engine=InnoDB default charset=utf8 comment '秒杀成功明细表';

