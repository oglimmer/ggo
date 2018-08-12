#!/bin/bash

trap cleanup 2
set -e

# returns the JDK version.
# 8 for 1.8.0_nn, 9 for 9-ea etc, and "no_java" for undetected
# from https://stackoverflow.com/questions/7334754/correct-way-to-check-java-version-from-bash-script
jdk_version() {
  local result
  local java_cmd
  if [[ -n $(type -p java) ]]
  then
    java_cmd=java
  elif [[ (-n "$JAVA_HOME") && (-x "$JAVA_HOME/bin/java") ]]
  then
    java_cmd="$JAVA_HOME/bin/java"
  fi
  local IFS=$'\n'
  # remove \r for Cygwin
  local lines=$("$java_cmd" -Xms32M -Xmx32M -version 2>&1 | tr '\r' '\n')
  if [[ -z $java_cmd ]]
  then
    result=no_java
  else
    for line in $lines; do
      if [[ (-z $result) && ($line = *"version \""*) ]]
      then
        local ver=$(echo $line | sed -e 's/.*version "\(.*\)"\(.*\)/\1/; 1q')
        # on macOS, sed doesn't support '?'
        if [[ $ver = "1."* ]]
        then
          result=$(echo $ver | sed -e 's/1\.\([0-9]*\)\(.*\)/\1/; 1q')
        else
          result=$(echo $ver | sed -e 's/\([0-9]*\)\(.*\)/\1/; 1q')
        fi
      fi
    done
  fi
  echo "$result"
}

cleanup()
{
	echo "****************************************************************"
	echo "Stopping software.....please wait...."
	echo "****************************************************************"

  ALL_COMPONENTS=(tomcat)
  for keepRunningAllElement in "${ALL_COMPONENTS[@]}"; do
    IFS=',' read -r -a array <<< "$KEEP_RUNNING"
    found=0
    for keepRunningToFindeElement in "${array[@]}"; do
      if [ "$keepRunningAllElement" == "$keepRunningToFindeElement" ]; then
        echo "Not stopping $keepRunningAllElement!"
        found=1
      fi
    done
    if [ "$found" -eq 0 ]; then

      if [ "$keepRunningAllElement" == "tomcat" ]; then
        echo "Stopping $keepRunningAllElement ..."
        if [ "$TYPE_SOURCE_TOMCAT" == "download" ]; then
         localrun/apache-tomcat-$TOMCAT_VERSION/bin/shutdown.sh
         rm -f .tomcat
        fi
        if [ "$TYPE_SOURCE_TOMCAT" == "docker" ]; then
          docker rm -f $containerId
          rm -f .tomcat
        fi
      
      fi
    fi
  done

	exit 0
}

#
# SECTION: HELP / USAGE
#

usage="$(basename "$0")
where:
  -h                         show this help text
  -b [local|docker:version]  build locally (default) or within a maven image on docker, the default image is 3-jdk-10
  -s                         skip any build
  -c [all|build]             clean local run directory, when a build is scheduled for execution it also does a full build
  -f                         tail the apache catalina log at the end
  -v                         start VirtualBox via vagrant, install all dependencies, ssh into the VM and run
  -k [component]             keep [all] or comma sperarated list of components running
  -t [component:type:[path|version]] run component inside [docker] container, [download] component (default) or [local] use installed component from path
  -j version                 set/overwrite java_home to a specific version, needs to be in format for java_home 1.8, 9, 10

Tested software versions:
  -b docker:[3-jdk-8|3-jdk-9|3-jdk-10] default 3-jdk-10
  -j any locally installed JDK, version needs to be compatible with /usr/lib/java_home
  -t tomcat:download:[7|8|9], default 9
  -t tomcat:docker:[7|8|9], default 9

Examples:
  -b local                          do a local build, would respect -j
  -b docker:3-jdk-10                do a docker based build, in this case use maven:3-jdk-10 image
  -t tomcat:local:/usr/lib/tomcat   reuse tomcat installation from /usr/lib/tomcat, would not start/stop this tomcat   
  -t tomcat:download:7              download latest version 7 tomcat and run this build within it, would respect -j
  -t tomcat:docker:7                start docker image tomcat:7 and run this build within it
"

#
# SECTION: RESOLVE PARAMETER
#

cd ${0%/*}

BUILD=local
while getopts ':hb:sc:fvk:t:j:' option; do
  case "$option" in
    h) echo "$usage"
       exit;;
    b) BUILD=$OPTARG;;
    s) SKIP_BUILD=YES;;
    c) 
       CLEAN=$OPTARG
       if [ "$CLEAN" != "all" -a "$CLEAN" != "build" ]; then
         echo "Illegal -c parameter" && exit 1
       fi
       ;;
    f) TAIL=YES;;
    v) VAGRANT=YES;;
    k) KEEP_RUNNING=$OPTARG;;
    t) TYPE_SOURCE=$OPTARG;;
    j) JAVA_VERSION=$OPTARG;;
    :) printf "missing argument for -%s\n" "$OPTARG" >&2
       echo "$usage" >&2
       exit 1;;
   \?) printf "illegal option: -%s\n" "$OPTARG" >&2
       echo "$usage" >&2
       exit 1;;
  esac
done
shift $((OPTIND - 1))
TYPE_PARAM="$1"

#TYPE_SOURCE
TYPE_SOURCE_TOMCAT=download
IFS=',' read -r -a array <<< "$TYPE_SOURCE"
for typeSourceElement in "${array[@]}"; do
  IFS=: read comp type pathOrVersion <<< "$typeSourceElement"
  if [ "$comp" == "tomcat" ]; then
    TYPE_SOURCE_TOMCAT=$type
    if [ "$TYPE_SOURCE_TOMCAT" == "local" ]; then
      TYPE_SOURCE_TOMCAT_PATH=$pathOrVersion
    else
      TYPE_SOURCE_TOMCAT_VERSION=$pathOrVersion
    fi
  else
    echo "Illegal component in -t" && exit 1
  fi
done
if [ "$TYPE_SOURCE_TOMCAT" == "download" ]; then
  if [ -z "$TYPE_SOURCE_TOMCAT_VERSION" ]; then
    TYPE_SOURCE_TOMCAT_VERSION="9"
  fi
  # find latest tomcat version for $TYPE_SOURCE_TOMCAT_VERSION
  if [ "$(uname)" == "Linux" ]; then
    GREP_PERL_MODE="-P"
  fi
  TOMCAT_BASE_URL="http://mirror.vorboss.net/apache/tomcat"
  TOMCAT_VERSION_PRE=$(curl -s "$TOMCAT_BASE_URL/tomcat-$TYPE_SOURCE_TOMCAT_VERSION/"|grep -m1 -o $GREP_PERL_MODE "<a href=\"v\d*.\d*.\d*" || echo "__________9.0.10")
  TOMCAT_VERSION=${TOMCAT_VERSION_PRE:10}
  TOMCAT_URL=$TOMCAT_BASE_URL/tomcat-$TYPE_SOURCE_TOMCAT_VERSION/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz
fi

#JAVA_VERSION
if [ -n "$JAVA_VERSION" ]; then
  export JAVA_HOME=$(/usr/libexec/java_home -v $JAVA_VERSION)
fi

# check for dependencies
mvn --version 1>/dev/null || exit 1
java -version 2>/dev/null || exit 1
curl --version 1>/dev/null || exit 1


if [ "$VAGRANT" == "YES" -a "$VAGRANT_IGNORE" != "YES" ]; then
  mkdir -p localrun
  cd localrun
  cat <<-EOF > Vagrantfile
# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure("2") do |config|
  config.vm.box = "ubuntu/xenial64"
  config.vm.network "forwarded_port", guest: 8080, host: 8080
  config.vm.synced_folder "../", "/share_host"
  config.vm.provider "virtualbox" do |vb|
    vb.memory = "1024"
  end
  config.vm.provision "shell", inline: <<-SHELL
    apt-get update
    apt-get install -y maven openjdk-8-jdk-headless npm phantomjs
    ln -s /usr/bin/nodejs /usr/bin/node
    npm install -g jasmine phantomjs-prebuilt
    echo "Now continue with..."
    echo "\$ cd /share_host"
    echo "\$ ./run_local.sh -f"
    echo "...then browse to http://localhost:8080/grid"
  SHELL
end
EOF
  vagrant up
  vagrant ssh -c "cd /share_host && ./run_local.sh -f"
  exit 1
fi


# clean if requested
if [ -n "$CLEAN" ]; then
  if [ "$CLEAN" == "all" ]; then
	  rm -rf localrun
  fi
	MVN_CLEAN=clean
fi

# prepare env
mkdir -p localrun

#build
if [ "$SKIP_BUILD" != "YES" ]; then
  if [ "$BUILD" == "local" ]; then
    mvn $MVN_CLEAN $MVN_OPTS package
  elif [[ "$BUILD" == docker* ]]; then
    IFS=: read mainType dockerVersion <<< "$BUILD"
    if [ -z "$dockerVersion" ]; then
      dockerVersion=3-jdk-10
    fi

    mkdir -p localrun/dockerbuild
    cat <<-EOFDOCK > localrun/dockerbuild/Dockerfile
FROM maven:$dockerVersion
RUN apt-get update && \
    if [ "\$(cat /etc/debian_version)" = "9.5" ]; then \
      curl -sL https://deb.nodesource.com/setup_6.x | bash -; apt-get  -qy install nodejs phantomjs; \
      else apt-get -qy install npm phantomjs; fi && \
    apt-get clean && \
    rm -rf /tmp/* /var/tmp/* /var/lib/apt/archive/* /var/lib/apt/lists/*
RUN npm install -g jasmine phantomjs-prebuilt
ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
CMD ["mvn"]
EOFDOCK
    docker build --tag maven_build:$dockerVersion localrun/dockerbuild/

    docker run --rm -v "$(pwd)":/usr/src/build -v $(pwd)/localrun/.m2:/root/.m2 -w /usr/src/build maven_build:$dockerVersion mvn $MVN_CLEAN $MVN_OPTS package
  fi
fi

if [ "$TYPE_SOURCE_TOMCAT" == "download" ]; then
  if [ -f .tomcat ] && [ "$(<.tomcat)" != "download" ]; then
    echo "Tomcat running but started from different source type"
    exit 1
  fi
  # download tomcat
  if [ ! -f "/${TMPDIR:-/tmp}/apache-tomcat-$TOMCAT_VERSION.tar" ]; then
  	curl -s $TOMCAT_URL | gzip -d >/${TMPDIR:-/tmp}/apache-tomcat-$TOMCAT_VERSION.tar
  fi
  # extract tomcat
  if [ ! -d "./apache-tomcat-$TOMCAT_VERSION" ]; then
  	tar -xf /${TMPDIR:-/tmp}/apache-tomcat-$TOMCAT_VERSION.tar -C ./localrun
  fi
  targetPath=localrun/apache-tomcat-$TOMCAT_VERSION/webapps/
  cp web/target/grid.war $targetPath

  # start tomcat
  if [ ! -f ".tomcat" ]; then
    ./localrun/apache-tomcat-$TOMCAT_VERSION/bin/startup.sh
    echo "download">.tomcat
  fi

  tailCmd="tail -f ./localrun/apache-tomcat-$TOMCAT_VERSION/logs/catalina.out"
elif [ "$TYPE_SOURCE_TOMCAT" == "docker" ]; then
  if [ -f .tomcat ] && [ "$(<.tomcat)" == "download" ]; then
    echo "Tomcat running but started from different source type"
    exit 1
  fi
  # run in docker
  if [ -z "$TYPE_SOURCE_TOMCAT_VERSION" ]; then
    TYPE_SOURCE_TOMCAT_VERSION=9
  fi
  mkdir -p localrun/webapps
  targetPath=localrun/webapps/
  cp web/target/grid.war $targetPath
  if [ ! -f ".tomcat" ]; then
    containerId=$(docker run --rm -d -p 8080:8080 -e $(pwd)/localrun/webapps:/usr/lib/tomcat/webapps tomcat:$TYPE_SOURCE_TOMCAT_VERSION)
    echo "$containerId">.tomcat
  else
    containerId=$(<.tomcat)
  fi
  tailCmd="docker logs -f $containerId"
elif [ "$TYPE_SOURCE_TOMCAT" == "local" ]; then
  if [ -f .tomcat ]; then
    echo "Tomcat running but started from different source type"
    exit 1
  fi
  # reuse existing tomcat
  targetPath=$TYPE_SOURCE_TOMCAT_PATH/webapps/
  cp web/target/grid.war $targetPath
  tailCmd="tail -f $TYPE_SOURCE_TOMCAT_PATH/logs/catalina.out"
else 
  echo "Illegal type for tomcat ($TYPE_SOURCE_TOMCAT) -t" && exit 1
fi

# waiting for ctrl-c
if [ "$TAIL" == "YES" ]; then
  $tailCmd
else
  echo "$tailCmd"
	echo "<return> to rebuild, ctrl-c to stop CouchDB and Tomcat"
  while true; do
	  read </dev/tty
    mvn package
    cp web/target/grid.war $targetPath
  done
fi

