package cn.overthinker.system.service.exam.impl;

import cn.overthinker.system.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.system.domain.exam.vo.ExamVO;
import cn.overthinker.system.mapper.exam.ExamMapper;
import cn.overthinker.system.service.exam.ExamService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        // 用传统xml写sql
        return examMapper.selectExamList(examQueryDTO);
    }
}
