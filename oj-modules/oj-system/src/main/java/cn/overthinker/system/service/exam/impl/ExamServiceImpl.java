package cn.overthinker.system.service.exam.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.overthinker.common.core.constants.Constants;
import cn.overthinker.common.core.enums.ResultCode;
import cn.overthinker.common.security.exception.ServiceException;
import cn.overthinker.system.domain.exam.Exam;
import cn.overthinker.system.domain.exam.ExamQuestion;
import cn.overthinker.system.domain.exam.dto.ExamAddDTO;
import cn.overthinker.system.domain.exam.dto.ExamEditDTO;
import cn.overthinker.system.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.system.domain.exam.dto.ExamQuestAddDTO;
import cn.overthinker.system.domain.exam.vo.ExamDetailVO;
import cn.overthinker.system.domain.exam.vo.ExamVO;
import cn.overthinker.system.domain.question.Question;
import cn.overthinker.system.domain.question.vo.QuestionVO;
import cn.overthinker.system.manager.ExamCacheManager;
import cn.overthinker.system.mapper.exam.ExamMapper;
import cn.overthinker.system.mapper.exam.ExamQuestionMapper;
import cn.overthinker.system.mapper.question.QuestionMapper;
import cn.overthinker.system.service.exam.ExamService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamQuestionMapper, ExamQuestion> implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private ExamQuestionMapper examQuestionMapper;

    @Autowired
    private ExamCacheManager examCacheManager;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        // 用传统xml写sql
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public String add(ExamAddDTO examAddDTO) {
        checkExamSaveParams(examAddDTO, null);
        Exam exam = new Exam();
        BeanUtil.copyProperties(examAddDTO, exam);
        examMapper.insert(exam);
        return exam.getExamId().toString();
    }

    @Override
    public int delete(Long examId) {
        Exam exam = getExam(examId);
        checkExam(exam);
        examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId));
        return examMapper.deleteById(examId);
    }


    @Override
    public boolean questionAdd(ExamQuestAddDTO examQuestAddDTO) {
        Exam exam = getExam(examQuestAddDTO.getExamId());
        checkExam(exam);
        Set<Long> questionIdSet = examQuestAddDTO.getQuestionIdSet();
        if (CollectionUtil.isEmpty(questionIdSet)) {
            return true;
        }
        List<Question> questionList = questionMapper.selectBatchIds(questionIdSet);
        if (CollectionUtil.isEmpty(questionList) || questionList.size() < questionIdSet.size()) {
            throw new ServiceException(ResultCode.EXAM_QUESTION_NOT_EXISTS);
        }
        return saveExamQuestion(exam, questionIdSet);
    }

    @Override
    public int questionDelete(Long examId, Long questionId) {
        Exam exam = getExam(examId);
        checkExam(exam);
        return examQuestionMapper.delete(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId)
                .eq(ExamQuestion::getQuestionId, questionId));
    }

    @Override
    public ExamDetailVO detail(Long examId) {
        ExamDetailVO examDetailVO = new ExamDetailVO();
        Exam exam = getExam(examId);
        BeanUtil.copyProperties(exam, examDetailVO);
        List<ExamQuestion> examQuestionList = examQuestionMapper.selectList(new LambdaQueryWrapper<ExamQuestion>()
                .select(ExamQuestion::getQuestionId)
                .eq(ExamQuestion::getExamId, examId)
                .orderByAsc(ExamQuestion::getQuestionOrder));
        if (CollectionUtil.isEmpty(examQuestionList)) {
            //返回详情  只包含竞赛的基本信息
            return examDetailVO;
        }
        List<Long> questionIdList = examQuestionList.stream().map(ExamQuestion::getQuestionId).toList();

        //select  * from  tb_question question_id in (1,2,3)
        List<Question> questionList = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId, Question::getTitle, Question::getDifficulty)
                .in(Question::getQuestionId, questionIdList));
//        List<QuestionVO> questionVOList = new ArrayList<>();
        List<QuestionVO> questionVOList = BeanUtil.copyToList(questionList, QuestionVO.class);
        examDetailVO.setExamQuestionList(questionVOList);
        return examDetailVO;
    }

    @Override
    public int edit(ExamEditDTO examEditDTO) {
        Exam exam = getExam(examEditDTO.getExamId());
        checkExam(exam);
        checkExamSaveParams(examEditDTO, examEditDTO.getExamId());
        exam.setTitle(examEditDTO.getTitle());
        exam.setStartTime(examEditDTO.getStartTime());
        exam.setEndTime(examEditDTO.getEndTime());
        return examMapper.updateById(exam);
    }

    @Override
    public int publish(Long examId) {
        Exam exam = getExam(examId);
        if (exam.getEndTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_IS_FINISHED);
        }
        // select count(0) from tb_exam_question where exam_id = #{examId}
        Long count = examQuestionMapper.selectCount(new LambdaQueryWrapper<ExamQuestion>()
                .eq(ExamQuestion::getExamId, examId));
        if (count == null || count <= 0) {
            throw new ServiceException(ResultCode.EXAM_NOT_HAS_QUESTION);
        }
        exam.setStatus(Constants.TRUE);
        //要将新发布的竞赛数据存储到redis  e:t:l    e:d:examId
        examCacheManager.addCache(exam);
        return examMapper.updateById(exam);
    }

    @Override
    public int cancelPublish(Long examId) {
        Exam exam = getExam(examId);
        checkExam(exam);
        if (exam.getEndTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_IS_FINISHED);
        }
        exam.setStatus(Constants.FALSE);
        examCacheManager.deleteCache(examId);
        return examMapper.updateById(exam);
    }


    private void checkExamSaveParams(ExamAddDTO examSaveDTO, Long examId) {
        // 竞赛标题是否重复进行判断，以及竞赛的合理时间范围
        List<Exam> examList = examMapper.selectList(new LambdaQueryWrapper<Exam>()
                .eq(Exam::getTitle, examSaveDTO.getTitle())
                .ne(examId != null, Exam::getExamId, examId));
        if (CollectionUtil.isNotEmpty(examList)) {
            throw new ServiceException(ResultCode.FAILED_EXAM_EXISTS);
        }

        if (examSaveDTO.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_BEFORE_CURREN_TIME);
        }
        if (examSaveDTO.getStartTime().isAfter(examSaveDTO.getEndTime())) {
            throw new ServiceException(ResultCode.EXAM_START_TIME_AFTER_END_TIME);
        }
    }

    private void checkExam(Exam exam) {
        if (exam.getStartTime().isBefore(LocalDateTime.now())) {
            throw new ServiceException(ResultCode.EXAM_STARTED);
        }
    }

    private boolean saveExamQuestion(Exam exam, Set<Long> questionIdSet) {
        int num = 1;
        List<ExamQuestion> examQuestionList = new ArrayList<>();
        for (Long questionId : questionIdSet) {
            ExamQuestion examQuestion = new ExamQuestion();
            examQuestion.setExamId(exam.getExamId());
            examQuestion.setQuestionId(questionId);
            examQuestion.setQuestionOrder(num++);
            examQuestionList.add(examQuestion);
        }
        return saveBatch(examQuestionList);
    }

    private Exam getExam(Long examId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new ServiceException(ResultCode.EXAM_NOT_EXISTS);
        }
        return exam;
    }


}
