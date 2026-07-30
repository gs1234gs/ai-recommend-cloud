# ============================================================
# 批量构建所有微服务 Docker 镜像
# 使用方法：在 PowerShell 中执行  .\build-all.ps1
# ============================================================

$ErrorActionPreference = "Continue"
$BuildArgs = @(
    @{ Module = "gsy-gateway-cloud/gateway-server-app";  Image = "gsy-gateway:latest"  },
    @{ Module = "gsy-system-cloud/system-app";           Image = "gsy-system:latest"   },
    @{ Module = "gsy-ai-cloud/ai-app";                   Image = "gsy-ai:latest"       },
    @{ Module = "gsy-goods-cloud/goods-app";             Image = "gsy-goods:latest"    },
    @{ Module = "gsy-order-cloud/order-app";             Image = "gsy-order:latest"    },
    @{ Module = "gsy-behavior-cloud/behavior-app";       Image = "gsy-behavior:latest" },
    @{ Module = "gsy-upload-cloud";                       Image = "gsy-upload:latest"   },
    @{ Module = "gsy-review-cloud/review-app";           Image = "gsy-review:latest"   }
)

$total = $BuildArgs.Count
$current = 0
$failed = @()

foreach ($item in $BuildArgs) {
    $current++
    $WriteProgress = "{0}/{1}  Building  {2}  (MODULE={3})" -f $current, $total, $item.Image, $item.Module
    Write-Host $WriteProgress -ForegroundColor Cyan
    Write-Host ("-" * ($WriteProgress.Length)) -ForegroundColor DarkGray

    docker build --build-arg MODULE=$item.Module -t $item.Image .
    if ($LASTEXITCODE -ne 0) {
        Write-Host "FAILED: $($item.Image)" -ForegroundColor Red
        $failed += $item.Image
    } else {
        Write-Host "OK: $($item.Image)" -ForegroundColor Green
    }
    Write-Host ""
}

Write-Host ("=" * 50) -ForegroundColor Yellow
if ($failed.Count -eq 0) {
    Write-Host "All $total images built successfully!" -ForegroundColor Green
} else {
    Write-Host "Failed images: $($failed -join ', ')" -ForegroundColor Red
}
Write-Host ("=" * 50) -ForegroundColor Yellow