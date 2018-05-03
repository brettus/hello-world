@ECHO OFF
SET "INTERVIEW=%~dp0"
SET "INTERVIEW_BIN=%INTERVIEW%bin"
CD /D "%INTERVIEW%"

START "ConEmu" "%INTERVIEW_BIN%\ConEmu\ConEmu64.exe"^
 -reuse^
 -LoadCfgFile "%INTERVIEW_BIN%\ConEmu\ConEmu.xml"^
 -WndW 140^
 -WndH 40^
 -BufferHeight 3000^
 -ct^
 -Palette "<Twilight>"^
 -NoUpdate^
 -Here^
 -cmd @%INTERVIEW_BIN%\git-bash-node.task