# 使用说明
# 进入项目文件夹终端输入：.\push.ps1 "fix: 修复远程同步路径"
[CmdletBinding()]
param(
    [string]$msg
)

# 赋值语句必须放在 param 块后面
$OutputEncoding = [System.Text.Encoding]::UTF8

$date = Get-Date -Format "yyyy-MM-dd"
if ([string]::IsNullOrEmpty($msg)) {
    $fullMsg = "[$date] [Daily Update]"
} else {
    $fullMsg = "[$date] $msg"
}

# --- 下面是 Git 执行逻辑 ---
git add .

git commit -m $fullMsg
if ($LASTEXITCODE -ne 0) {
    Write-Host "⚠️ 没有新改动，跳过 commit" -ForegroundColor Yellow
}

Write-Host "🚀 推送到 GitHub..." -ForegroundColor Cyan
git push github master

Write-Host "🚀 推送到 Gitee..." -ForegroundColor Cyan
git push gitee master

Write-Host "`n✅ 同步成功：$fullMsg" -ForegroundColor Green