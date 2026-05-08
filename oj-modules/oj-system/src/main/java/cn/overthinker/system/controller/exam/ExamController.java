package cn.overthinker.system.controller.exam;

import cn.overthinker.common.core.controller.BaseController;
import cn.overthinker.common.core.domain.R;
import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.system.domain.exam.dto.ExamAddDTO;
import cn.overthinker.system.domain.exam.dto.ExamEditDTO;
import cn.overthinker.system.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.system.domain.exam.dto.ExamQuestAddDTO;
import cn.overthinker.system.domain.exam.vo.ExamDetailVO;
import cn.overthinker.system.service.exam.ExamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    //新增竞赛
    @PostMapping("/add")
    public R<String> add(@Validated @RequestBody ExamAddDTO examAddDTO) {
        return R.ok(examService.add(examAddDTO));
    }

    //新增题目信息
    @PostMapping("/question/add")
    public R<Void> questionAdd(@RequestBody ExamQuestAddDTO examQuestAddDTO) {
        return toR(examService.questionAdd(examQuestAddDTO));
    }

    //获取竞赛详情
    @GetMapping("/detail")
    public R<ExamDetailVO> detail(Long examId) {
        return R.ok(examService.detail(examId));
    }

    //编辑竞赛基本信息
    @PutMapping("/edit")
    public R<Void> edit(@Validated @RequestBody ExamEditDTO examEditDTO) {
        return toR(examService.edit(examEditDTO));
    }
}
