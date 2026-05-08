package cn.overthinker.system.domain.exam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamEditDTO extends ExamAddDTO {

    @NotNull(message = "竞赛ID不能为空")
    private Long examId;
}
