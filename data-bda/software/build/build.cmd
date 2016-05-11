@echo off
@rem ******************************************
@rem **** Command file to invoke build.xml ****
@rem ******************************************
setlocal
cls
if "%JAVA_HOME%" == "" (
    set JAVA_HOME=C:\Apps\Java\jdk1.6.0_03
)
if "%1" == "" (
    echo.
    echo Available targets are:
    echo.
    echo   clean        -- Remove classes directory for clean build
    echo   all          -- Normal build of application
    echo   upgrade      -- Normal build of application with local deployment
    echo   tar          -- Tar executables for distribution
    goto DONE
)
if "%1" == "all" (
    ant build:all
    goto DONE
)
if "%1" == "upgrade" (
    ant ant deploy:local:upgrade
    goto DONE
)
if "%1" == "clean" (
    if exist ..\target\*.* (
       rmdir /Q /S ..\target
    )
    ant clean
    goto DONE
)
if "%1" == "tar" (
    ant tar
    goto DONE
)
:DONE
endlocal