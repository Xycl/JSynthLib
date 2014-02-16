#!/bin/sh
echo "Starting upload to Sourceforge"
sftp -b /dev/stdin packe01,jsynthlib@frs.sourceforge.net <<EOF
cd /home/pfs/project/jsynthlib/
mkdir $1
cd $1
put target/jsynthlib-$1-executable.jar*
put target/jsynthlib-$1-sources.jar*
put target/jsynthlib-$1-javadoc.jar*
put target/JSynthLib-$1.dmg*
put target/JSynthLib-$1-setup.exe*
put src/main/resources/README

quit

echo "Upload to Sourceforge finished"
