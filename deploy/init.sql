# 账号密码
create table if not exists tb_sys_user
(
    user_id      bigint unsigned not null comment '用户id（主键）',
    user_account varchar(20)     not null comment '账号',
    nick_name    varchar(20) comment '昵称',
    password     varchar(60)     not null comment '密码',
    create_by    bigint unsigned not null comment '创建人',
    create_time  datetime        not null comment '创建时间',
    update_by    bigint unsigned comment '更新人',
    update_time  datetime comment '更新时间',
    primary key (`user_id`),
    unique key `idx_user_count` (`user_account`)
);


# 题库管理
# B端：列表功能，新增题目，编辑，删除
# C端：题目列表功能，题目热榜列表，答题，竞赛开始答题，竞赛练习
# 题目数据 -----> 数据库mysql   设计表结构
# 设计的表结构：满足需求 避免冗余设计 考虑今后发展
create table tb_question
(
    question_id   bigint unsigned not null comment '题目id',
    title         varchar(50)     not null comment '题目标题',
    difficulty    tinyint         not null comment '题目难度1:简单  2：中等 3：困难',
    time_limit    int             not null comment '时间限制',
    space_limit   int             not null comment '空间限制',
    content       varchar(1000)   not null comment '题目内容',
    question_case varchar(1000) comment '题目用例',
    default_code  varchar(500)    not null comment '默认代码块',
    main_func     varchar(500)    not null comment 'main函数',
    create_by     bigint unsigned not null comment '创建人',
    create_time   datetime        not null comment '创建时间',
    update_by     bigint unsigned comment '更新人',
    update_time   datetime comment '更新时间',
    primary key (`question_id`)
);