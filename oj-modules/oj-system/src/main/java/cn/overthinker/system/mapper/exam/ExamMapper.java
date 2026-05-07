package cn.overthinker.system.mapper.exam;

import cn.overthinker.system.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.system.domain.exam.Exam;
import cn.overthinker.system.domain.exam.vo.ExamVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface ExamMapper extends BaseMapper<Exam> {
    List<ExamVO> selectExamList(ExamQueryDTO examQueryDTO);
}
