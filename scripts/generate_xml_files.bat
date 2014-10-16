@ECHO OFF

pushd ..

IF NOT .%1 == . (
 ECHO This script performs two tasks:
 ECHO  1. Extracts XML device and driver files based on existing legacy code
 ECHO  2. Generates FXML stubs from XML single drivers. These FXML stubs are
 ECHO  supposed to be further enhanced in Scene Builder afterwards to create
 ECHO  a user friendly GUI
 ECHO 
 ECHO During the script execution you will be prompted for data input
 ECHO You can exit prematurely with CTRL+C
 GOTO :end
)

SET /p XMLEXTRACTION="Extract XML from existing driver: (y/n) " %=%

SET XMLEXTRACTION_DIR=XMLEXTRACTION

IF /i "%XMLEXTRACTION%" == "Y" GOTO xmlextraction

:fxmlgeneration

SET /p FXMLGENERATION="Generate FXML from existing XML files: (y/n) " %=%

IF /i "%FXMLGENERATION%" == "Y" GOTO fxmlgeneration2

GOTO end

:fxmlgeneration2

SET /p PACKAGE_NAME="Please enter package name: " %=%
SET FXMLGENERATION_DIR=FXMLGENERATION
MKDIR %FXMLGENERATION_DIR%

ECHO %XMLEXTRACTION_DIR%
IF EXIST .\%XMLEXTRACTION_DIR% (GOTO move_generated_files) ELSE (GOTO get_fxml_data)

:fxmlgeneration3

ECHO FXML output is stored in %FXMLGENERATION_DIR%

SET /p MOVE_FILES="Move FXML file into package structure (This will move the generated FXML file into your source tree): (y/n) " %=%

IF /i "%MOVE_FILES:~,1%" EQU "Y" GOTO movefxmlfiles

GOTO end

:move_generated_files
SET /p MOVE_FILES="Move XML file into package structure (This will move the generated FXML file into your source tree and is required in order to generate the FXML files): (y/n) " %=%

IF NOT /i "%MOVE_FILES:~,1%" EQU "Y" (
	ECHO Cannot proceed with FXML generation without moving XML files into src package structure.
	GOTO end
)

MOVE %XMLEXTRACTION_DIR%\*.properties %FXMLGENERATION_DIR%
MOVE %XMLEXTRACTION_DIR%\*.* src\main\java\%PACKAGE_NAME:.=\%
RD /s /q %XMLEXTRACTION_DIR%

call mvn package exec:java -DskipTests -Dexec.mainClass="org.jsynthlib.utils.editor.FXMLGenerator" -Dexec.classpathScope=runtime -Dexec.args="%FXMLGENERATION_DIR%"

GOTO fxmlgeneration3 

:get_fxml_data

SET /p FILE_NAME_PREFIX="Please enter file name prefix: " %=%
call mvn package exec:java -DskipTests -Dexec.mainClass="org.jsynthlib.utils.editor.FXMLGenerator" -Dexec.classpathScope=runtime -Dexec.args="%PACKAGE_NAME% %FILE_NAME_PREFIX% %FXMLGENERATION_DIR%"

GOTO fxmlgeneration3

:xmlextraction

SET /p MANUFACTURER="Please enter manufacturer name (Same as the manufacturer name as stated in the Device class): " %=%
SET /p DEVICE="Please enter device name (The name displayed in \"Synthesizer Device Install\" dialog): " %=%
MKDIR %XMLEXTRACTION_DIR%

call mvn test-compile exec:java -Dexec.mainClass="org.jsynthlib.device.viewcontroller.widgets.XMLExtractor" -Dexec.classpathScope=test -Dexec.args="\"%MANUFACTURER%\" \"%DEVICE%\" %XMLEXTRACTION_DIR%"

ECHO XML output is stored in %XMLEXTRACTION_DIR%
GOTO fxmlgeneration

:movefxmlfiles

DEL %FXMLGENERATION_DIR%\*.properties
MOVE %FXMLGENERATION_DIR%\*.* src\main\java\%PACKAGE_NAME:.=\%
RD /s /q %FXMLGENERATION_DIR% 

:end

popd

ECHO Exiting...