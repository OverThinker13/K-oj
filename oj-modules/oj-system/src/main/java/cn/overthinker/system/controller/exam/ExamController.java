package cn.overthinker.system.controller.exam;

import cn.overthinker.common.core.controller.BaseController;
import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.system.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.system.service.exam.ExamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/exam")
@Tag(name = "竞赛管理接口")
public class ExamController extends BaseController {

    @Autowired
    private ExamService examService;

    // 获取竞赛列表
    @GetMapping("/list")
    public TableDataInfo list(ExamQueryDTO examQueryDTO) {
        return getTableDataInfo(examService.list(examQueryDTO));
    }

}
