# 使用说明
# 进入项目文件夹终端输入：.\push.ps1 "fix: 修复远程同步路径"

# 解决中文乱码
$OutputEncoding = [System.Text.Encoding]::UTF8

# 接收参数
param([string]$msg)

# 生成日期格式
$date = Get-Date -Format "yyyy-MM-dd"
if ([string]::IsNullOrEmpty($msg)) {
    $fullMsg = "[$date] [Daily Update]"
} else {
    $fullMsg = "[$date] $msg"
}

# Git 添加所有文件
git add .

# 提交（兼容所有 PowerShell，不会报错）
git commit -m $fullMsg
if ($LASTEXITCODE -ne 0) {
    Write-Host "⚠️ 没有检测到新改动，跳过 commit" -ForegroundColor Yellow
}

# 推送到 GitHub
Write-Host "🚀 正在推送到 GitHub..." -ForegroundColor Cyan
git push github master

# 推送到 Gitee
Write-Host "🚀 正在推送到 Gitee..." -ForegroundColor Cyan
git push gitee master

# 完成
Write-Host "`n✅ 同步完成: $fullMsg" -ForegroundColor Green