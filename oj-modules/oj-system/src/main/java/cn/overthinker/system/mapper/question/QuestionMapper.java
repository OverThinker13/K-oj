package cn.overthinker.system.mapper.question;

import cn.overthinker.system.domain.question.Question;
import cn.overthinker.system.domain.question.dto.QuestionQueryDTO;
import cn.overthinker.system.domain.question.vo.QuestionVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    List<QuestionVO> selectQuestionList(QuestionQueryDTO questionQueryDTO);
}
