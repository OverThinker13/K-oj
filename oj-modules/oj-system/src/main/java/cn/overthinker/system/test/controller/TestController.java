package cn.overthinker.system.test.controller;

import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.redis.service.RedisService;
import cn.overthinker.system.domain.SysUser;
import cn.overthinker.system.test.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @Autowired
    private TestService testService;

    @Autowired
    private RedisService redisService;

    // /test/list 查询tb_test的数据
    @GetMapping("/list")
    public List<?> list() {
        return testService.list();
    }

    @PostMapping("/add")
    public String add() {
        return testService.add();
    }

    @GetMapping("/redisAddAndGet")
    public String redisAddAndGet() {
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount("redisTest");
        redisService.setCacheObject("u", sysUser);
        SysUser u = redisService.getCacheObject("u", SysUser.class);
        return u.toString();
    }

    @GetMapping("apifoxtest")
    public R<String> apifoxtest(String apiId) {
        R<String> r = new R<>();
        r.setCode(ResultCode.SUCCESS.getCode());
        r.setMsg(ResultCode.SUCCESS.getMsg());
        r.setData("apifoxtest:" + apiId);
        return r;
    }

    @GetMapping("/log")
    public String log() {
        log.info("我是info级别日志");
        log.error("我是error级别日志");
        return "log测试";
    }


}
