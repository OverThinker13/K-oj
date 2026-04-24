package cn.overthinker.system.test.service.Impl;

import cn.overthinker.system.test.domain.TestDomain;
import cn.overthinker.system.test.mapper.TestMapper;
import cn.overthinker.system.test.service.TestService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    private TestMapper testMapper;

    @Override
    public List<?> list() {
        return testMapper.selectList(new LambdaQueryWrapper<>());
    }

    @Override
    public String add() {
        TestDomain testDomain = new TestDomain();
        testDomain.setTitle("test");
        testDomain.setContent("测试雪花主键生成");
        testMapper.insert(testDomain);
        return "success";
    }


}
