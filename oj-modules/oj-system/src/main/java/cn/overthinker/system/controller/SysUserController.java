package cn.overthinker.system.controller;

import cn.overthinker.common.core.domain.R;
import cn.overthinker.system.domain.LoginDTO;
import cn.overthinker.system.domain.SysUserSaveDTO;
import cn.overthinker.system.domain.SysUserVO;
import cn.overthinker.system.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统管理员控制器
 * 提供管理员登录及增删改查相关接口
 */
@RestController
@Slf4j
@RequestMapping("/sysUser")
@Tag(name = "管理员接口")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    /**
     * 管理员登录
     * 验证账号密码，成功返回 token，失败返回对应错误码
     *
     * @param loginDTO 登录信息（账号、密码）
     * @return 操作结果
     */
    @Operation(summary = "管理员登陆", description = "根据账号密码进行管理员登陆")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3102", description = "用户不存在")
    @ApiResponse(responseCode = "3103", description = "用户名或密码错误")
    @PostMapping("/login")
    public R<String> login(@RequestBody LoginDTO loginDTO) {
        return sysUserService.login(loginDTO.getUserAccount(), loginDTO.getPassword());
    }

    /**
     * 新增管理员
     *
     * @param sysUserSaveDTO 管理员信息
     * @return 操作结果
     */
    @PostMapping("/add")
    @Operation(summary = "新增管理员", description = "根据提供信息新增管理员")
    @ApiResponse(responseCode = "1000", description = "操作成功")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户已存在")
    public R<Void> add(@RequestBody SysUserSaveDTO sysUserSaveDTO) {
        return null;
    }

    /**
     * 删除管理员
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "通过用户id删除⽤⼾")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.PATH, description = "用户ID")
    })
    @ApiResponse(responseCode = "1000", description = "成功删除用户")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户不存在")
    public R<Void> delete(@PathVariable Long userId) {
        return null;
    }

    /**
     * 查询管理员详情
     *
     * @param userId 用户ID（必填）
     * @param sex    用户性别（可选，用于筛选）
     * @return 用户详情
     */
    @Operation(summary = "用户详情", description = "根据查询条件查询用户详情")
    @GetMapping("/detail")
    @Parameters(value = {
            @Parameter(name = "userId", in = ParameterIn.QUERY, description = "用户ID"),
            @Parameter(name = "sex", in = ParameterIn.QUERY, description = "用户性别")
    })
    @ApiResponse(responseCode = "1000", description = "成功获取用户⽤信息")
    @ApiResponse(responseCode = "2000", description = "服务繁忙请稍后重试")
    @ApiResponse(responseCode = "3101", description = "用户不存在")
    public R<SysUserVO> detail(@RequestParam(required = true) Long userId, @RequestParam(required = false) String sex) {
        return null;
    }
}


