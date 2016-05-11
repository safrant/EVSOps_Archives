@echo off
@rem **********************************************
@rem **** Command file to invoke Validate util ****
@rem **********************************************
setlocal enabledelayedexpansion
set JAVA_HOME=C:\Apps\Java\jdk1.5.0_14
echo %~nx0

@rem Add jars to classpath
set CWD=%~dp0
set JAVAOPTS=-Xms1024M -Xmx1024M -XX:PermSize=256M -XX:MaxPermSize=256M

@rem Run validate app
java %JAVAOPTS% -jar deploy.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
cd %CWD%
:done
endlocal
