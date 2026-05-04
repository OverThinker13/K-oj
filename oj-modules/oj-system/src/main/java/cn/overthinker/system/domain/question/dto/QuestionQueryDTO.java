package cn.overthinker.system.domain.question.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionQueryDTO {
    private Integer difficulty;
    private String title;
    private Integer pageSize = 10;   //每页的数据  必传
    private Integer pageNum = 1;    //第几页     必传
}
