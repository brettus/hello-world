SET "ROOTDIR=%~dp0"
cd /d %ROOTDIR%

SET "BIN_DIR=%ROOTDIR%bin"

REM http://gedankenverlust.blogspot.de/2012/05/java-environment-variables-definitive.html
PUSHD "%BIN_DIR%"
for /f %%x in ('dir jdk* /AD /B /O:-D') do @SET "JDK_DIR=%%x"
POPD
SET "JAVA_HOME=%BIN_DIR%\%JDK_DIR%"
SET "JDK_HOME=%JAVA_HOME%"
SET "JRE_HOME=%JAVA_HOME%\jre"
SET "CLASSPATH=.;%JAVA_HOME%\lib;%JAVA_HOME%\jre\lib"

REM For Git
SET "GIT_ROOT=%BIN_DIR%\Git"
SET "HOME=%ROOTDIR%\home"

SET "PATH=%JAVA_HOME%\bin;%GIT_ROOT%\cmd;C:\Windows\system32;C:\Windows"

start "Eclipse" "%BIN_DIR%\eclipse\eclipse.exe" -vm "%JAVA_HOME%\bin" -user "%HOME%" -data "%ROOTDIR%eclipse_workspace" -vmargs "-Duser.home=%HOME%"