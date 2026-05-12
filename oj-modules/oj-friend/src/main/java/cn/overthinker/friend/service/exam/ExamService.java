package cn.overthinker.friend.service.exam;

import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.friend.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.friend.domain.exam.dto.ExamRankDTO;
import cn.overthinker.friend.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamService {
    List<ExamVO> list(ExamQueryDTO examQueryDTO);

//    TableDataInfo redisList(ExamQueryDTO examQueryDTO);
//
//    TableDataInfo rankList(ExamRankDTO examRankDTO);
//
//    String getFirstQuestion(Long examId);
//
//    String preQuestion(Long examId, Long questionId);
//
//    String nextQuestion(Long examId, Long questionId);
}
