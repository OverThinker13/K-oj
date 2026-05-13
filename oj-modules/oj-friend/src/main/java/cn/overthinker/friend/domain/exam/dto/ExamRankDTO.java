package cn.overthinker.friend.domain.exam.dto;


import cn.overthinker.common.core.domain.dto.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamRankDTO extends PageQueryDTO {

    private Long examId;
}
