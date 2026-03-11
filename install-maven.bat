@echo off
set MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip
set DOWNLOAD_DIR=%TEMP%\maven-download
set INSTALL_DIR=C:\apache-maven-3.9.6

echo 正在下载 Maven...
if not exist "%DOWNLOAD_DIR%" mkdir "%DOWNLOAD_DIR%"
powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%DOWNLOAD_DIR%\maven.zip'"

echo 正在解压 Maven...
powershell -Command "Expand-Archive -Path '%DOWNLOAD_DIR%\maven.zip' -DestinationPath 'C:\'"

echo 正在重命名目录...
if exist "C:\apache-maven-3.9.6" (
    echo 目录已存在，跳过重命名
) else (
    ren "C:\apache-maven-3.9.6-bin" "apache-maven-3.9.6" 2>nul || ren "C:\apache-maven-3.9.6" "apache-maven-3.9.6" 2>nul
)

echo 设置环境变量...
setx M2_HOME "%INSTALL_DIR%"
setx PATH "%PATH%;%INSTALL_DIR%\bin"

echo 验证安装...
call mvn -version

echo.
echo Maven 安装完成！
echo M2_HOME: %INSTALL_DIR%
echo 请重新打开命令提示符以使用 mvn 命令

pause