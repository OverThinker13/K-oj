package cn.overthinker.system.domain.exam.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;

@Setter
@Getter
public class ExamQuestAddDTO {

    private Long examId;

    private LinkedHashSet<Long> questionIdSet;
}
