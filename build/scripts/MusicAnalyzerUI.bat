@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  MusicAnalyzerUI startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and MUSIC_ANALYZER_UI_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\MusicAnalyzerUI.jar;%APP_HOME%\lib\commons-csv-1.10.0.jar;%APP_HOME%\lib\commons-math4-legacy-4.0-beta1.jar;%APP_HOME%\lib\spring-boot-starter-web-3.2.3.jar;%APP_HOME%\lib\spring-boot-starter-test-3.2.3.jar;%APP_HOME%\lib\commons-statistics-distribution-1.0.jar;%APP_HOME%\lib\commons-numbers-combinatorics-1.1.jar;%APP_HOME%\lib\commons-numbers-gamma-1.1.jar;%APP_HOME%\lib\commons-numbers-angle-1.1.jar;%APP_HOME%\lib\commons-numbers-field-1.1.jar;%APP_HOME%\lib\commons-numbers-quaternion-1.1.jar;%APP_HOME%\lib\commons-math4-legacy-core-4.0-beta1.jar;%APP_HOME%\lib\commons-numbers-fraction-1.1.jar;%APP_HOME%\lib\commons-math4-core-4.0-beta1.jar;%APP_HOME%\lib\commons-numbers-core-1.1.jar;%APP_HOME%\lib\commons-numbers-complex-1.1.jar;%APP_HOME%\lib\commons-numbers-rootfinder-1.1.jar;%APP_HOME%\lib\commons-numbers-arrays-1.1.jar;%APP_HOME%\lib\commons-rng-simple-1.5.jar;%APP_HOME%\lib\commons-rng-sampling-1.5.jar;%APP_HOME%\lib\commons-rng-core-1.5.jar;%APP_HOME%\lib\commons-rng-client-api-1.5.jar;%APP_HOME%\lib\commons-math4-legacy-exception-4.0-beta1.jar;%APP_HOME%\lib\spring-boot-starter-json-3.2.3.jar;%APP_HOME%\lib\spring-boot-starter-3.2.3.jar;%APP_HOME%\lib\spring-boot-starter-tomcat-3.2.3.jar;%APP_HOME%\lib\spring-webmvc-6.1.4.jar;%APP_HOME%\lib\spring-web-6.1.4.jar;%APP_HOME%\lib\spring-boot-test-autoconfigure-3.2.3.jar;%APP_HOME%\lib\spring-boot-test-3.2.3.jar;%APP_HOME%\lib\json-path-2.9.0.jar;%APP_HOME%\lib\jakarta.xml.bind-api-4.0.1.jar;%APP_HOME%\lib\json-smart-2.5.0.jar;%APP_HOME%\lib\assertj-core-3.24.2.jar;%APP_HOME%\lib\awaitility-4.2.0.jar;%APP_HOME%\lib\hamcrest-2.2.jar;%APP_HOME%\lib\mockito-junit-jupiter-5.7.0.jar;%APP_HOME%\lib\junit-jupiter-params-5.10.2.jar;%APP_HOME%\lib\junit-jupiter-engine-5.10.2.jar;%APP_HOME%\lib\junit-jupiter-api-5.10.2.jar;%APP_HOME%\lib\junit-platform-engine-1.10.2.jar;%APP_HOME%\lib\junit-platform-commons-1.10.2.jar;%APP_HOME%\lib\junit-jupiter-5.10.2.jar;%APP_HOME%\lib\mockito-core-5.7.0.jar;%APP_HOME%\lib\jsonassert-1.5.1.jar;%APP_HOME%\lib\spring-test-6.1.4.jar;%APP_HOME%\lib\spring-boot-autoconfigure-3.2.3.jar;%APP_HOME%\lib\spring-boot-3.2.3.jar;%APP_HOME%\lib\spring-context-6.1.4.jar;%APP_HOME%\lib\spring-aop-6.1.4.jar;%APP_HOME%\lib\spring-beans-6.1.4.jar;%APP_HOME%\lib\spring-expression-6.1.4.jar;%APP_HOME%\lib\spring-core-6.1.4.jar;%APP_HOME%\lib\xmlunit-core-2.9.1.jar;%APP_HOME%\lib\spring-boot-starter-logging-3.2.3.jar;%APP_HOME%\lib\jakarta.annotation-api-2.1.1.jar;%APP_HOME%\lib\snakeyaml-2.2.jar;%APP_HOME%\lib\jackson-datatype-jsr310-2.15.4.jar;%APP_HOME%\lib\jackson-module-parameter-names-2.15.4.jar;%APP_HOME%\lib\jackson-annotations-2.15.4.jar;%APP_HOME%\lib\jackson-core-2.15.4.jar;%APP_HOME%\lib\jackson-datatype-jdk8-2.15.4.jar;%APP_HOME%\lib\jackson-databind-2.15.4.jar;%APP_HOME%\lib\tomcat-embed-websocket-10.1.19.jar;%APP_HOME%\lib\tomcat-embed-core-10.1.19.jar;%APP_HOME%\lib\tomcat-embed-el-10.1.19.jar;%APP_HOME%\lib\micrometer-observation-1.12.3.jar;%APP_HOME%\lib\logback-classic-1.4.14.jar;%APP_HOME%\lib\log4j-to-slf4j-2.21.1.jar;%APP_HOME%\lib\jul-to-slf4j-2.0.12.jar;%APP_HOME%\lib\slf4j-api-2.0.12.jar;%APP_HOME%\lib\jakarta.activation-api-2.1.2.jar;%APP_HOME%\lib\accessors-smart-2.5.0.jar;%APP_HOME%\lib\byte-buddy-1.14.9.jar;%APP_HOME%\lib\byte-buddy-agent-1.14.9.jar;%APP_HOME%\lib\objenesis-3.3.jar;%APP_HOME%\lib\android-json-0.0.20131108.vaadin1.jar;%APP_HOME%\lib\spring-jcl-6.1.4.jar;%APP_HOME%\lib\micrometer-commons-1.12.3.jar;%APP_HOME%\lib\asm-9.3.jar;%APP_HOME%\lib\opentest4j-1.3.0.jar;%APP_HOME%\lib\logback-core-1.4.14.jar;%APP_HOME%\lib\log4j-api-2.21.1.jar


@rem Execute MusicAnalyzerUI
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %MUSIC_ANALYZER_UI_OPTS%  -classpath "%CLASSPATH%" Cs214Project %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable MUSIC_ANALYZER_UI_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%MUSIC_ANALYZER_UI_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
