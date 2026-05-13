package cn.overthinker.friend.domain.exam.dto;

import cn.overthinker.common.core.domain.dto.PageQueryDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQueryDTO extends PageQueryDTO {

    private String title;

    private String startTime;

    private String endTime;

    private Integer type; //0 未开赛  1 历史竞赛
}
