package cn.overthinker.system.service.exam;

import cn.overthinker.system.domain.exam.dto.ExamAddDTO;
import cn.overthinker.system.domain.exam.dto.ExamEditDTO;
import cn.overthinker.system.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.system.domain.exam.dto.ExamQuestAddDTO;
import cn.overthinker.system.domain.exam.vo.ExamDetailVO;
import cn.overthinker.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamService {

    List<ExamVO> list(ExamQueryDTO examQueryDTO);

    String add(ExamAddDTO examAddDTO);

    int delete(Long examId);

    boolean questionAdd(ExamQuestAddDTO examQuestAddDTO);

    int questionDelete(Long examId, Long questionId);

    ExamDetailVO detail(Long examId);

    int edit(ExamEditDTO examEditDTO);

    int publish(Long examId);
}
