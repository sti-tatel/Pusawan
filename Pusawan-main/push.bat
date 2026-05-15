@echo off
REM commit only when there are changes
git status --porcelain | findstr /R /C:".*" >nul
if %errorlevel%==0 (
  git add .
  git commit -m "update"
) else (
  echo No changes to commit.
)

REM cleanup local git object database before pushing
git gc --aggressive --prune=now

git push origin master