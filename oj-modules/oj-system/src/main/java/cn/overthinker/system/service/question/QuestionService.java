package cn.overthinker.system.service.question;

import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.system.domain.question.dto.QuestionQueryDTO;

public interface QuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);
}
