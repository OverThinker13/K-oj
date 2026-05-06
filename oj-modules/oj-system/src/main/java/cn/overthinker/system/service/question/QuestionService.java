package cn.overthinker.system.service.question;

import cn.overthinker.system.domain.question.dto.QuestionAddDTO;
import cn.overthinker.system.domain.question.dto.QuestionEditDTO;
import cn.overthinker.system.domain.question.dto.QuestionQueryDTO;
import cn.overthinker.system.domain.question.vo.QuestionDetailVO;

import java.util.List;

public interface QuestionService {
    List<?> list(QuestionQueryDTO questionQueryDTO);

    int add(QuestionAddDTO questionAddDTO);

    QuestionDetailVO detail(Long questionId);

    int edit(QuestionEditDTO questionEditDTO);

    int delete(Long questionId);
}
