@echo off
echo Linking remote branches...
echo (If you see errors like "fatal: A branch named 'end' already exists.", you can ignore them.
echo It just means the branches have already been linked.)
echo.
echo ---------------------------------------------------------
git branch --track end origin/end
git branch --track start5 origin/start5
git branch --track start4 origin/start4
git branch --track start3 origin/start3
git branch --track start2 origin/start2
git branch --track start1 origin/start1
echo ---------------------------------------------------------