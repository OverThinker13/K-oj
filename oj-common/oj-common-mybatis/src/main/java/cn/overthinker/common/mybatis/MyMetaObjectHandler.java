package cn.overthinker.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//MyBatis-Plus 的自动填充工具

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        //创建人  获取当前用户id  如何获取当前调用接口的用户id呢
        this.strictInsertFill(metaObject, "createBy", Long.class, 2036034227126351367L);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        //创建人  获取当前用户id  如何获取当前调用接口的用户id呢
        this.strictUpdateFill(metaObject, "updateBy", Long.class, 2036034227126351367L);
    }
}
