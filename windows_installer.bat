@ECHO OFF

SET SIGNTOOL="c:\Program Files\Microsoft SDKs\Windows\v7.1\Bin\signtool.exe"
SET RES_HACKER="c:\Program Files (x86)\Resource Hacker\ResHacker.exe"
SET MK_NSIS="C:\Program Files (x86)\NSIS\Bin\makensis.exe"

ECHO Create synth.jar
echo f | xcopy /F /Y target\jsynthlib-%1-executable.jar target\synth.jar

ECHO Creating exe from jar
iexpress /N src\main\installer\Windows\jsynthlib.SED

ECHO Replacing icon of Windows launcher executable
%RES_HACKER% -addoverwrite target\jsynthlib.exe, target\jsynthlib.exe, src\main\installer\Windows\JSLIcon.ico, ICONGROUP, MAINICON, 0

%SIGNTOOL% sign /f %2 /p %3 /t http://timestamp.comodoca.com/authenticode target\jsynthlib.exe

%MK_NSIS% "/XOutFile ..\..\..\..\target\JSynthLib-%1-setup.exe" /DVERSION=%1 src\main\installer\Windows\setup.nsi

%SIGNTOOL% sign /f %2 /p %3 /t http://timestamp.verisign.com/scripts/timstamp.dll target\JSynthLib-%1-setup.exe
