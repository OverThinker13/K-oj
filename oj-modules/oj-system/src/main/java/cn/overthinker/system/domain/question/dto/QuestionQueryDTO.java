package cn.overthinker.system.domain.question.dto;

import cn.overthinker.common.core.domain.dto.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionQueryDTO extends PageQueryDTO {
    private Integer difficulty;
    private String title;
}
