package cn.overthinker.system.service.question.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.system.domain.question.dto.QuestionQueryDTO;
import cn.overthinker.system.domain.question.vo.QuestionVO;
import cn.overthinker.system.mapper.question.QuestionMapper;
import cn.overthinker.system.service.question.QuestionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public List<QuestionVO> list(QuestionQueryDTO questionQueryDTO) {
        PageHelper.startPage(questionQueryDTO.getPageNum(), questionQueryDTO.getPageSize());
        // 用传统xml写sql
        return questionMapper.selectQuestionList(questionQueryDTO);
    }
}
