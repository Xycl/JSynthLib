#!/bin/sh

cd ..

echo Create directories
mkdir target/JSynthLib.app
mkdir target/JSynthLib.app/Contents/

echo Copying default content
cp -r src/main/installer/MacOsX/  target/JSynthLib.app/Contents/

mkdir target/JSynthLib.app/Contents/Resources/Java/

JARFILE=jsynthlib-$1-executable.jar
INFO_PLIST=target/JSynthLib.app/Contents/Info.plist

echo Copying jar file
cp target/$JARFILE target/JSynthLib.app/Contents/Resources/Java/
chmod 700 target/JSynthLib.app/Contents/MacOS/JavaApplicationStub

echo Version: $1
/usr/libexec/PlistBuddy -c "Set :CFBundleVersion $1" $INFO_PLIST
/usr/libexec/PlistBuddy -c "Set :Java:ClassPath \$JAVAROOT/$JARFILE" $INFO_PLIST

echo Creating .dmg
hdiutil create -ov -srcfolder target/JSynthLib.app target/JSynthLib-$1.dmg
hdiutil internet-enable -yes target/JSynthLib-$1.dmg