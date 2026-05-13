package cn.overthinker.friend.service.exam.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.overthinker.common.core.domain.TableDataInfo;
import cn.overthinker.friend.domain.exam.dto.ExamQueryDTO;
import cn.overthinker.friend.domain.exam.vo.ExamVO;
import cn.overthinker.friend.manager.ExamCacheManager;
import cn.overthinker.friend.manager.UserCacheManager;
import cn.overthinker.friend.mapper.exam.ExamMapper;
import cn.overthinker.friend.mapper.user.UserExamMapper;
import cn.overthinker.friend.service.exam.ExamService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ExamServiceImpl implements ExamService {

    @Autowired
    private ExamMapper examMapper;

    @Autowired
    private ExamCacheManager examCacheManager;

    @Autowired
    private UserCacheManager userCacheManager;

    @Autowired
    private UserExamMapper userExamMapper;

    @Override
    public List<ExamVO> list(ExamQueryDTO examQueryDTO) {
        PageHelper.startPage(examQueryDTO.getPageNum(), examQueryDTO.getPageSize());
        return examMapper.selectExamList(examQueryDTO);
    }

    @Override
    public TableDataInfo redisList(ExamQueryDTO examQueryDTO) {
        //从redis中获取竞赛列表的数据
        Long total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        List<ExamVO> examVOList;
        if (total == null || total <= 0) {
            //没数据刷新缓存同步数据
            examVOList = list(examQueryDTO);
            examCacheManager.refreshCache(examQueryDTO.getType(), null);
            total = new PageInfo<>(examVOList).getTotal();
        } else {
            //有数据直接从缓存获取数据
            examVOList = examCacheManager.getExamVOList(examQueryDTO, null);
            total = examCacheManager.getListSize(examQueryDTO.getType(), null);
        }
        if (CollectionUtil.isEmpty(examVOList)) {
            return TableDataInfo.empty();
        }
//        assembleExamVOList(examVOList);
        return TableDataInfo.success(examVOList, total);
    }

//    private void assembleExamVOList(List<ExamVO> examVOList) {
//        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
//        List<Long> userExamIdList = examCacheManager.getAllUserExamList(userId);
//        if (CollectionUtil.isEmpty(userExamIdList)) {
//            return;
//        }
//        for (ExamVO examVO : examVOList) {
//            if (userExamIdList.contains(examVO.getExamId())) {
//                examVO.setEnter(true);
//            }
//        }
//    }
}
