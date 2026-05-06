package cn.overthinker.system.service.question.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.security.exception.ServiceException;
import cn.overthinker.system.domain.question.Question;
import cn.overthinker.system.domain.question.dto.QuestionAddDTO;
import cn.overthinker.system.domain.question.dto.QuestionEditDTO;
import cn.overthinker.system.domain.question.dto.QuestionQueryDTO;
import cn.overthinker.system.domain.question.vo.QuestionDetailVO;
import cn.overthinker.system.domain.question.vo.QuestionVO;
import cn.overthinker.system.mapper.question.QuestionMapper;
import cn.overthinker.system.service.question.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
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

    @Override
    public int add(QuestionAddDTO questionAddDTO) {
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .eq(Question::getTitle, questionAddDTO.getTitle()));

        if (CollectionUtil.isNotEmpty(questionList)) {
            throw new ServiceException(ResultCode.FAILED_ALREADY_EXISTS);
        }
        Question question = new Question();
        // 需要进行模型转换
        BeanUtils.copyProperties(questionAddDTO, question);
        return questionMapper.insert(question);
    }

    @Override
    public QuestionDetailVO detail(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        QuestionDetailVO questionDetailVO = new QuestionDetailVO();
        BeanUtils.copyProperties(question, questionDetailVO);
        return questionDetailVO;
    }

    @Override
    public int edit(QuestionEditDTO questionEditDTO) {
        Question oldQuestion = questionMapper.selectById(questionEditDTO.getQuestionId());
        if (oldQuestion == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        oldQuestion.setTitle(questionEditDTO.getTitle());
        oldQuestion.setDifficulty(questionEditDTO.getDifficulty());
        oldQuestion.setTimeLimit(questionEditDTO.getTimeLimit());
        oldQuestion.setSpaceLimit(questionEditDTO.getSpaceLimit());
        oldQuestion.setContent(questionEditDTO.getContent());
        oldQuestion.setQuestionCase(questionEditDTO.getQuestionCase());
        oldQuestion.setDefaultCode(questionEditDTO.getDefaultCode());
        oldQuestion.setMainFunc(questionEditDTO.getMainFunc());
        return questionMapper.updateById(oldQuestion);
    }
}
