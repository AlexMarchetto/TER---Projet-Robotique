@echo off
setlocal

REM ============================================================
REM Script de build / clean pour un controller Java Webots
REM A placer a la racine du dossier controller.
REM Exemple :
REM src/controllers/FourWheelsCollisionAvoidance/controller.bat
REM ============================================================

set WEBOTS_HOME=C:\Program Files\Webots
set CONTROLLER_JAR=%WEBOTS_HOME%\lib\controller\java\Controller.jar

if not exist "%CONTROLLER_JAR%" (
    echo ERREUR : Controller.jar introuvable.
    echo Chemin teste :
    echo "%CONTROLLER_JAR%"
    echo.
    echo Verifie que Webots est bien installe dans :
    echo "%WEBOTS_HOME%"
    pause
    exit /b 1
)

if "%1"=="clean" goto clean
if "%1"=="build" goto build
if "%1"=="rebuild" goto rebuild

echo Usage :
echo   controller.bat clean
echo   controller.bat build
echo   controller.bat rebuild
echo.
pause
exit /b 0

:clean
echo Suppression des fichiers .class...
del /S /Q *.class 2>nul
echo Clean termine.
exit /b 0

:build
echo Compilation du controller Java...
javac -encoding UTF-8 -classpath "%CONTROLLER_JAR%" -d . *.java api\core\*.java api\behavior\*.java api\motors\*.java api\sensors\*.java api\actuators\*.java api\world\*.java api\tasks\*.java api\state\*.java api\utils\*.java

if errorlevel 1 (
    echo.
    echo ERREUR : La compilation a echoue.
    pause
    exit /b 1
)

echo.
echo Build termine avec succes.
exit /b 0

:rebuild
call "%~f0" clean
call "%~f0" build
exit /b %errorlevel%