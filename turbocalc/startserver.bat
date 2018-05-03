@echo off
set errorlevel=
cls

call mvn compile
call mvn package
call forego start -f Procfile-windows