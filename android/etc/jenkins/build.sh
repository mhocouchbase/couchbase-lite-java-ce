#!/bin/bash
#
# Build Couchbase Lite Android, Community Edition
#

# These versions must match the versions in lib/build.gradle
NDK_VERSION='21.3.6528147'
CMAKE_VERSION='3.10.2.4988404'
BUILD_TOOLS_VERSION='30.0.2'

MAVEN_URL="http://proget.build.couchbase.com/maven2/cimaven"


function usage() {
    echo "Usage: $0 <sdk path> <build number> <reports dir>"
    exit 1
}

if [ "$#" -ne 3 ]; then
    usage
fi

SDK_HOME="$1"
if [ -z "$SDK_HOME" ]; then
    usage
fi

BUILD_NUMBER="$2"
if [ -z "$BUILD_NUMBER" ]; then
    usage
fi

REPORTS="$3"
if [ -z "REPORTS" ]; then
    usage
fi

SDK_MGR="${SDK_HOME}/tools/bin/sdkmanager"
STATUS=0

echo "======== BUILD Couchbase Lite Android, Community Edition v`cat ../../version.txt`-${BUILD_NUMBER}"

echo "======== Install Toolchain"
yes | ${SDK_MGR} --licenses > /dev/null 2>&1
${SDK_MGR} --install "build-tools;${BUILD_TOOLS_VERSION}"
${SDK_MGR} --install "cmake;${CMAKE_VERSION}"
${SDK_MGR} --install "ndk;${NDK_VERSION}"

# The Jenkins script has already put passwords into local.properties
cat <<EOF >> local.properties
sdk.dir=${SDK_HOME}
ndk.dir=${SDK_HOME}/ndk/${NDK_VERSION}
cmake.dir=${SDK_HOME}/cmake/${CMAKE_VERSION}
EOF

echo "======== Check"
./gradlew ciCheck -PbuildNumber="${BUILD_NUMBER}" || STATUS=5

if  [ $STATUS -eq 0 ]; then
    echo "======== Build"
    ./gradlew ciBuild -PbuildNumber="${BUILD_NUMBER}" || STATUS=6
fi

if  [ $STATUS -eq 0 ]; then
    echo "======== Publish artifacts"
    ./gradlew ciPublish -PbuildNumber="${BUILD_NUMBER}" -PmavenUrl="${MAVEN_URL}" || STATUS=7
fi

echo "======== Publish reports"
pushd lib/build
zip -r "${REPORTS}/analysis-reports-android" reports
popd

echo "======== BUILD COMPLETE (${STATUS})"
exit $STATUS

