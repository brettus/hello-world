@echo off
echo Setting environment variables...
@echo -----------------------------------------
SET "JAVA_HOME=C:\Program Files\Java\jdk1.7.0_67"
echo * Using Java %JAVA_HOME%
SET "MAVEN_HOME=C:\apache-maven-3.2.3"
echo * Using Maven %MAVEN_HOME%
SET "GIT_HOME=C:\Program Files (x86)\Git"
echo * Using Git %GIT_HOME%
SET "PATH=C:\bin;%MAVEN_HOME%\bin;%JAVA_HOME%\bin;%GIT_HOME%\bin;C:\Windows\system32;C:\Windows"
SET "TERM=msys"
@echo -----------------------------------------
@echo Done