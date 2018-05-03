@echo off

@set "BATPATH=%~dp0"
@set "PATCHPATH=%BATPATH%patches"

@echo This will delete old local branches, re-create them, 
@echo then delete old branches from GitHub and re-create them as well
@echo.
@echo Ctrl+C now if you don't want to do this!
@echo.
@pause

git checkout -f master

@echo.
@echo ------------------------------------------------------------
@echo.
@echo Deleting old local branches
git branch -D end start5 start4 start3 start2 start1

@echo.
@echo ------------------------------------------------------------
@echo.
@echo Setting up end
git checkout -B end

@echo.
@echo ------------------------------------------------------------
@echo.
@echo Setting up start5
git checkout -B start5
git apply "%PATCHPATH%\end-to-start5.patch"
git add .
git commit -m "Simplify code"


@echo.
@echo ------------------------------------------------------------
@echo.
@echo Setting up start4
git checkout -B start4
git apply "%PATCHPATH%\start5-to-start4.patch"
git add .
git commit -m "Simplify code"


@echo.
@echo ------------------------------------------------------------
@echo.
@echo Setting up start3
git checkout -B start3
git apply "%PATCHPATH%\start4-to-start3.patch"
git add .
git commit -m "Simplify code"


@echo.
@echo ------------------------------------------------------------
@echo.
@echo Setting up start2
git checkout -B start2
git apply --ignore-space-change --ignore-whitespace "%PATCHPATH%\start3-to-start2.patch"
git add .
git commit -m "Simplify code"


@echo.
@echo ------------------------------------------------------------
@echo.
@echo Setting up start1
git checkout -B start1
git apply --ignore-space-change --ignore-whitespace "%PATCHPATH%\start2-to-start1.patch"
git add .
git commit -m "Simplify code"


@echo.
@echo ------------------------------------------------------------
@echo.
@echo About to delete GitHub branches and push changes to GitHub!
@pause
@echo.
@echo.
@echo Deleting...
git push github :end :start5 :start4 :start3 :start2 :start1
@echo.
@echo.
@echo Pushing new branches...
git push github end start5 start4 start3 start2 start1

@echo.
@echo.
@echo Done!
git checkout master