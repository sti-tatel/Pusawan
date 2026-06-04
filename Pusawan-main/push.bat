@echo off

REM add *.zip to .gitignore only if not already there
findstr /x /c:"*.zip" .gitignore >nul 2>&1
if %errorlevel% neq 0 (
  echo *.zip>> .gitignore
)

REM remove any zip files from tracking (silently)
git rm --cached *.zip 2>nul

REM nuke commit history (run once, then remove this block)
git checkout --orphan clean-start
git add -A
git commit -m "Initial commit"
git branch -D master
git branch -m master
git push -f origin master

REM stage and commit if there are changes
git status --porcelain | findstr /R /C:".*" >nul
if %errorlevel%==0 (
  git add .
  git commit -m "update"
) else (
  echo No changes to commit.
)

REM push
git push origin master