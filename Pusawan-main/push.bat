@echo off

REM remove any large zip files from tracking
git rm --cached *.zip 2>nul
echo "*.zip" >> .gitignore

REM stage and commit if there are changes
git status --porcelain | findstr /R /C:".*" >nul
if %errorlevel%==0 (
  git add .
  git commit -m "update"
) else (
  echo No changes to commit.
)

REM rewrite history to remove any zip files from all past commits
git filter-branch --force --index-filter "git rm --cached --ignore-unmatch *.zip" --prune-empty --tag-name-filter cat -- --all

REM clean up git object database
git reflog expire --expire=now --all
git gc --aggressive --prune=now

REM force push to overwrite remote history
git push origin master --force