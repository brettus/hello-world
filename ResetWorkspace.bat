@echo off
SET "ROOTDIR=%~dp0"
cd /d %ROOTDIR%

SET "ECLIPSE_WORKSPACE=%ROOTDIR%eclipse_workspace"
SET "OUTPUT_DATA=%ROOTDIR%data"
SET "PATH=%ROOTDIR%bin\;C:\Windows\system32;C:\Windows""

@echo Make sure Eclipse and the command prompt are closed!
pause

if exist "%ECLIPSE_WORKSPACE%" (
    @echo.
    @echo Removing old workspace...
    del /s /f /q "%ECLIPSE_WORKSPACE%\" > nul
    rd /s /q "%ECLIPSE_WORKSPACE%\" > nul
)

if not exist "%ECLIPSE_WORKSPACE%" goto :extract
echo Unable to delete old workspace directory (%ECLIPSE_WORKSPACE%) - delete it manually, then run this script again
pause
exit

:extract

@echo.
@echo Preparing Eclipse Workspace...
for /f %%x in ('dir "%OUTPUT_DATA%\eclipse_workspace_*.7z" /B /O:-D') do @SET "WORKSPACE_ZIP=%%x"
@echo     %WORKSPACE_ZIP%
"%SETUPHOME%7z.exe" x "-o%ECLIPSE_WORKSPACE%" "%OUTPUT_DATA%\%WORKSPACE_ZIP%" > nul

@echo Done!
pause