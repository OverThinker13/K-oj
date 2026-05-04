package cn.overthinker.system.service.question.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.system.domain.question.dto.QuestionQueryDTO;
import cn.overthinker.system.domain.question.vo.QuestionVO;
import cn.overthinker.system.mapper.question.QuestionMapper;
import cn.overthinker.system.service.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public TableDataInfo list(QuestionQueryDTO questionQueryDTO) {
        // 用传统xml写sql
        List<QuestionVO> questionVOList = questionMapper.selectQuestionList(questionQueryDTO);
//        questionVOList == null || questionVOList.isEmpty()
        if (CollectionUtil.isEmpty(questionVOList)) {
            return TableDataInfo.empty();
        }
        return TableDataInfo.success(questionVOList, questionVOList.size());
    }
}
