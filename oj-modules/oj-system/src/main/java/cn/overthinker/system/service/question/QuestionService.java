package cn.overthinker.system.service.question;

import cn.overthinker.system.domain.question.dto.QuestionAddDTO;
import cn.overthinker.system.domain.question.dto.QuestionQueryDTO;

import java.util.List;

public interface QuestionService {
    List<?> list(QuestionQueryDTO questionQueryDTO);

    int add(QuestionAddDTO questionAddDTO);
}
