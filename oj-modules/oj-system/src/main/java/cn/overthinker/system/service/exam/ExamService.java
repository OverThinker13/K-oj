package cn.overthinker.system.service.exam;

import cn.overthinker.system.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.system.domain.exam.vo.ExamVO;

import java.util.List;

public interface ExamService {

    List<ExamVO> list(ExamQueryDTO examQueryDTO);
}
