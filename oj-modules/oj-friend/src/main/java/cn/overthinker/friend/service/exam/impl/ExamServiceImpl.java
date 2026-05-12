package cn.overthinker.friend.service.exam.impl;

import cn.overthinker.friend.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.friend.domain.exam.vo.ExamVO;
import cn.overthinker.friend.mapper.exam.ExamMapper;
import cn.overthinker.friend.service.exam.ExamService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

//    @Autowired
//    private ExamCacheManager examCacheManager;
//
//    @Autowired
//    private UserCacheManager userCacheManager;
//
//    @Autowired
//    private UserExamMapper userExamMapper;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }
}
