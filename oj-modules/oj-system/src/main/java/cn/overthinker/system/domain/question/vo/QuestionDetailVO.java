package cn.overthinker.system.domain.question.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuestionDetailVO {
    private Long questionId;
    private String title;
    private Integer difficulty;
    private Long timeLimit;
    private Long spaceLimit;
    private String content;
    private String questionCase;
    private String defaultCode;
    private String mainFunc;
}
