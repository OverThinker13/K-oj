package cn.overthinker.job.mapper.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.overthinker.job.domain.user.UserExam;
import cn.overthinker.job.domain.user.UserScore;

import java.util.List;


public interface UserExamMapper extends BaseMapper<UserExam> {

    void updateUserScoreAndRank(List<UserScore> userScoreList);
}
