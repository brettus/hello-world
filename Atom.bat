SET "ROOTDIR=%~dp0"
cd /d %ROOTDIR%

SET "BIN_DIR=%ROOTDIR%bin"
SET "ATOM_BIN=%BIN_DIR%\Atom"

SET "ATOM_HOME=%ROOTDIR%home\.atom"
cd /d "%ATOM_BIN%"
START "Atom" ".\atom.exe"
