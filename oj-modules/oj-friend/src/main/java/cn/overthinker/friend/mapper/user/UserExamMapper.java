package cn.overthinker.friend.mapper.user;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.overthinker.friend.domain.exam.vo.ExamRankVO;
import cn.overthinker.friend.domain.exam.vo.ExamVO;
import cn.overthinker.friend.domain.user.UserExam;

import java.util.List;


public interface UserExamMapper extends BaseMapper<UserExam> {

    List<ExamVO> selectUserExamList(Long userId);

    List<ExamRankVO> selectExamRankList(Long examId);

}