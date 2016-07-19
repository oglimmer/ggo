#!/bin/sh
# (#) DEPENDENDCIES:
#  ROOT-DIR
#    + $PROJECT_NAME-release/build.sh (this file)
#    + repos/$PROJECT_NAME-build/ (clone of the $PROJECT_NAME-repository@master)
#    + server/ansible/ (clone of the master ansible repository)
# 
# (#) the pom.xml needs to have this (as the 'current' version is broken)
#   <plugin>
#   	<groupId>org.apache.maven.plugins</groupId>
#   	<artifactId>maven-release-plugin</artifactId>
#   	<version>3.0-r1585899</version>
#   </plugin>
#
# (#) the first tag ($PROJECT_NAME-0.1) needs to be created manually
#

PROJECT_NAME=ggo

cd ../repos/$PROJECT_NAME-build/

git fetch && git pull

LAST_MESSAGE=$(git log --format=%B -n 1)
$(echo $LAST_MESSAGE | grep -q "\[maven-release-plugin\] prepare for next development iteration")
LAST_MESSAGE_C1=$?
$(echo $LAST_MESSAGE | grep -q "\[maven-release-plugin\] prepare release $PROJECT_NAME-")
LAST_MESSAGE_C2=$?

if [ $LAST_MESSAGE_C1 -eq 0 -o $LAST_MESSAGE_C2 -eq 0 ]; then
	echo "No commits since last tag. No build needed."
	exit 1
fi

LATEST_MINOR_VERSION=$(git describe --abbrev=0 --tags | grep -o "[0-9]*$")
NEW_MINOR_VERSION=$((LATEST_MINOR_VERSION + 1))
NEW_MINOR_VERSION_PLUS_ONE=$((NEW_MINOR_VERSION + 1))

TAG=$PROJECT_NAME-0.$NEW_MINOR_VERSION
RELEASE=0.$NEW_MINOR_VERSION
DEV=0.$NEW_MINOR_VERSION_PLUS_ONE-SNAPSHOT

echo "********************************************************************"
echo "Create new release with:"
echo "TAG=$TAG"
echo "RELEASE=$RELEASE"
echo "DEV=$DEV"

mvn --batch-mode -Dtag=$TAG -DreleaseVersion=$RELEASE -DdevelopmentVersion=$DEV release:prepare

if [ $? -ne 0 ]; then
	exit 1
fi

find . -type f -name "pom.xml.releaseBackup" -delete
rm -f release.properties

git push

cd ../../server/ansible
sed -i -e 's/'$PROJECT_NAME'_version.*/'$PROJECT_NAME'_version: '$PROJECT_NAME'-'$RELEASE'/g' roles/$PROJECT_NAME/vars/main.yml

echo "Version in the UI:"
grep "$PROJECT_NAME_version" roles/$PROJECT_NAME/vars/main.yml

./deploy.sh -d production $PROJECT_NAME
