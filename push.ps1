# 用法: .\push.ps1 "完成了Swagger配置"
param([string]$msg)

$date = Get-Date -Format "yyyy-MM-dd"
# 如果没传参数，就用默认格式；传了就组合起来
if ([string]::IsNullOrEmpty($msg)) {
    $fullMsg = "[$date] [Daily Update]"
} else {
    $fullMsg = "[$date] $msg"
}

git add .
git commit -m $fullMsg
git push origin master
Write-Host "成功提交: $fullMsg" -ForegroundColor Green