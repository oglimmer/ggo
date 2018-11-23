#!/usr/bin/env bash

# DO NOT EDIT THIS FILE!
# Generated by fulgens (https://www.npmjs.com/package/fulgens)
# Version: 0.0.12

trap cleanup 2
set -e

#---------------------
# START - FunctionsBuilder

jdk_version() {

	# returns the JDK version.
	# 8 for 1.8.0_nn, 9 for 9-ea etc, and "no_java" for undetected
	# from https://stackoverflow.com/questions/7334754/correct-way-to-check-java-version-from-bash-script
	local result
	local java_cmd
	if [[ -n $(type -p java) ]]; then
		java_cmd=java
	elif [[ (-n "$JAVA_HOME") && (-x "$JAVA_HOME/bin/java") ]]; then
		java_cmd="$JAVA_HOME/bin/java"
	fi
	local IFS=$'\n'
	# remove \r for Cygwin
	local lines=$("$java_cmd" -Xms32M -Xmx32M -version 2>&1 | tr '\r' '\n')
	if [[ -z $java_cmd ]]; then
		result=no_java
	else
		for line in $lines; do
			if [[ (-z $result) && ($line == *"version \""*) ]]; then
				local ver=$(echo $line | sed -e 's/.*version "\(.*\)"\(.*\)/\1/; 1q')
				# on macOS, sed doesn't support '?'
				if [[ $ver == "1."* ]]; then
					result=$(echo $ver | sed -e 's/1\.\([0-9]*\)\(.*\)/\1/; 1q')
				else
					result=$(echo $ver | sed -e 's/\([0-9]*\)\(.*\)/\1/; 1q')
				fi
			fi
		done
	fi
	echo "$result"

}

# END - FunctionsBuilder
#---------------------

verbosePrint() {
	if [ "$VERBOSE" == "YES" ]; then
		echo -e "$1"
	fi
}

startDockerNetwork() {
	if [ -z "$DOCKER_NETWORKED_CHECKED" ]; then
		DOCKER_NETWORKED_CHECKED=YES
		if ! docker network ls | grep -s "gridgameonenet"; then
			verbosePrint "Starting docker network gridgameonenet on 10.198.119.0/24"
			docker network create -d bridge --subnet 10.198.119.0/24 --gateway 10.198.119.1 "gridgameonenet"
		else
			verbosePrint "Docker network gridgameonenet already running"
		fi
	fi
}

#---------------------
# START - CleanupBuilder

cleanup() {
	echo "****************************************************************"
	echo "Stopping software .....please wait...."
	echo "****************************************************************"
	set +e

	ALL_COMPONENTS=(tomcat)
	for componentToStop in "${ALL_COMPONENTS[@]}"; do
		IFS=',' read -r -a keepRunningArray <<<"$KEEP_RUNNING"
		componentFoundToKeepRunning=0
		for keepRunningToFindeElement in "${keepRunningArray[@]}"; do
			if [ "$componentToStop" == "$keepRunningToFindeElement" ]; then
				echo "Not stopping $componentToStop!"
				componentFoundToKeepRunning=1
			fi
		done
		if [ "$componentFoundToKeepRunning" -eq 0 ]; then

			if [ "$START_TOMCAT" = "YES" ]; then
				if [ "$componentToStop" == "tomcat" ]; then
					echo "Stopping $componentToStop ..."

					if [ "$TYPE_SOURCE_TOMCAT" == "docker" ]; then
						docker rm -f $dockerContainerIDtomcat
						rm -f .tomcatPid
					fi

					if [ "$TYPE_SOURCE_TOMCAT" == "download" ]; then
						./localrun/apache-tomcat-$TOMCAT_VERSION/bin/shutdown.sh
						rm -f .tomcatPid
					fi

				fi
			fi

		fi
	done

	exit 0
}

# END - CleanupBuilder
#---------------------

#---------------------
# START - OptionsBuilder

usage="
usage: $(basename "$0") [options] [<component(s)>]

Options:
  -h                         show this help text
  -s                         skip any build
  -S                         skip consistency check against Fulgensfile
  -c [all|build]             clean local run directory, when a build is scheduled for execution it also does a full build
  -k [component]             keep comma sperarated list of components running
  -t [component:type:[path|version]] run component inside [docker] container, [download] component or [local] use installed component from path
  -v                         enable Verbose
  -V                         start VirtualBox via vagrant, install all dependencies, ssh into the VM and run
  -f                         tail the apache catalina log at the end
  -j version                 macOS only: set/overwrite JAVA_HOME to a specific locally installed version, use format from/for: /usr/libexec/java_home [-V]
  
Url: http://localhost:8080/grid

Details for components:
ggo {Source:\"mvn\", Default-Type:\"local\", Version-Info: \"Tested with 3-jre-11\"}
  -t ggo:local #build local and respect -j
  -t ggo:docker:[TAG] #docker based build, default tag: latest, uses image http://hub.docker.com/_/maven
tomcat {Source:\"tomcat\", Default-Type:\"download:9\", Version-Info: \"Tested with 9 (slim)\"}
  -t tomcat:docker:[TAG] #start docker, default tag latest, uses image http://hub.docker.com/_/tomcat
  -t tomcat:download:[7|8|9] #start fresh downloaded tomcat, default version 9 and respect -j
  -t tomcat:local:TOMCAT_HOME_PATH #reuse tomcat installation from TOMCAT_HOME_PATH, does not start/stop this tomcat
"

cd "$(
	cd "$(dirname "$0")"
	pwd -P
)"
BASE_PWD=$(pwd)

BUILD=local
while getopts ':hsSc:k:x:t:vVfj:' option; do
	case "$option" in
	h)
		echo "$usage"
		exit
		;;
	s) SKIP_BUILD=YES ;;
	S) SKIP_HASH_CHECK=YES ;;
	c)
		CLEAN=$OPTARG
		if [ "$CLEAN" != "all" -a "$CLEAN" != "build" ]; then
			echo "Illegal -c parameter" && exit 1
		fi
		;;
	k) KEEP_RUNNING=$OPTARG ;;
	x) SKIP_STARTING=$OPTARG ;;
	t) TYPE_SOURCE=$OPTARG ;;
	v) VERBOSE=YES ;;

	V) VAGRANT=YES ;;

	f) TAIL=YES ;;

	j) JAVA_VERSION=$OPTARG ;;

	:)
		printf "missing argument for -%s\\n" "$OPTARG" >&2
		echo "$usage" >&2
		exit 1
		;;
	\\?)
		printf "illegal option: -%s\\n" "$OPTARG" >&2
		echo "$usage" >&2
		exit 1
		;;
	esac
done
shift $((OPTIND - 1))

if [ -z "$1" ]; then

	declare START_GGO=YES

	declare START_TOMCAT=YES

else
	ALL_COMPONENTS=(GGO TOMCAT)
	for comp in "$@"; do
		compUpper=$(echo $comp | awk '{print toupper($0)}')
		compValid=0
		for compDefined in "${ALL_COMPONENTS[@]}"; do
			if [ "$compDefined" = "$compUpper" ]; then
				compValid=1
			fi
		done
		if [ "$compValid" -eq 0 ]; then
			echo "Component $comp is invalid!"
			exit 1
		fi
		declare START_$compUpper=YES
	done
fi

# END - OptionsBuilder
#---------------------

if [ "$SKIP_HASH_CHECK" != "YES" ]; then
	if which md5 1>/dev/null; then
		declare SELF_HASH_MD5="b9da1cef71ab8c9bad95bdcdb43581ad"
		declare SOURCE_FILES=(Fulgensfile Fulgensfile.js)
		for SOURCE_FILE in ${SOURCE_FILES[@]}; do
			declare SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null && pwd)"
			if [ -f "$SCRIPT_DIR/$SOURCE_FILE" ]; then
				if [ "$SELF_HASH_MD5" != "$(md5 -q $SCRIPT_DIR/$SOURCE_FILE)" ]; then
					echo "$SOURCE_FILE doesn not match!"
					exit 1
				fi
			fi
		done
	fi
fi

#---------------------
# START - DependencycheckBuilder

mvn --version 1>/dev/null || exit 1
curl --version 1>/dev/null || exit 1
java -version 2>/dev/null || exit 1

# END - DependencycheckBuilder
#---------------------

# clean if requested
if [ -n "$CLEAN" ]; then
	if [ "$CLEAN" == "all" ]; then
		if [ "$VERBOSE" == "YES" ]; then echo "rm -rf localrun"; fi
		rm -rf localrun
	fi

fi

#---------------------
# START - GlobalVariablesBuilder

verbosePrint "DEFAULT: TYPE_SOURCE_GGO=local"
TYPE_SOURCE_GGO=local

verbosePrint "DEFAULT: TYPE_SOURCE_TOMCAT=download"
TYPE_SOURCE_TOMCAT=download

# END - GlobalVariablesBuilder
#---------------------

if [ "$(uname)" = "Linux" ]; then
	ADD_HOST_INTERNAL="--add-host host.docker.internal:$(ip -4 addr show scope global dev docker0 | grep inet | awk '{print $2}' | cut -d / -f 1)"
fi

mkdir -p localrun

f_deploy() {
	echo "No plugin defined f_deploy()"
}

#---------------------
# START - PrepareBuilder

if [ "$(uname)" == "Darwin" ]; then
	if [ -n "$JAVA_VERSION" ]; then
		export JAVA_HOME=$(/usr/libexec/java_home -v $JAVA_VERSION)
	fi
fi

if [ "$VAGRANT" == "YES" -a "$VAGRANT_IGNORE" != "YES" ]; then
	mkdir -p localrun
	cd localrun
	cat <<-EOF >Vagrantfile
		# -*- mode: ruby -*-
		# vi: set ft=ruby :
		
		Vagrant.configure("2") do |config|
		  config.vm.box = "ubuntu/xenial64"
		  config.vm.network "forwarded_port", guest: 8080, host: 8080
		  config.vm.synced_folder "../", "/share_host"
		  
		  config.vm.provider "virtualbox" do |vb|
		    vb.memory = "1536"
		    vb.cpus = 4
		  end
		  config.vm.provision "shell", inline: <<-SHELL
		  	
		    apt-get update    
		    
		      if [ "\$(cat /etc/*release|grep ^ID=)" = "ID=debian"  ]; then \\
		        if [ "\$(cat /etc/debian_version)" = "8.11" ]; then \\
		           curl -sL https://deb.nodesource.com/setup_6.x | bash -; apt-get -qy install nodejs maven openjdk-8-jdk-headless docker.io; \\
		        elif [ "\$(cat /etc/debian_version)" = "9.5" ]; then \\
		          curl -sL https://deb.nodesource.com/setup_6.x | bash -; apt-get -qy install nodejs maven openjdk-8-jdk-headless docker.io; \\
		        else curl -sL https://deb.nodesource.com/setup_10.x | bash -; apt-get -qy install nodejs maven openjdk-8-jdk-headless docker.io; fi \\
		      elif [ "\$(cat /etc/*release|grep ^ID=)" = "ID=ubuntu"  ]; then \\
		        curl -sL https://deb.nodesource.com/setup_10.x | bash -; apt-get -qy install nodejs maven openjdk-8-jdk-headless docker.io; \\
		      else \\
		        echo "only debian or ubuntu are supported."; \\
		        exit 1; \\
		      fi \\
		    
		    
		    npm install -g jasmine
		    echo "Now continue with..."
		    echo "\$ cd /share_host"
		    echo "\$ sudo ./run_local.sh -f"
		    echo "...then browse to http://localhost:8080/grid"
		  SHELL
		end
	EOF
	vagrant up
	if [ -f "../run_local.sh" ]; then
		vagrant ssh -c "cd /share_host && sudo ./run_local.sh -f"
	else
		echo "Save the fulgens output into a bash script (e.g. run_local.sh) and use it inside the new VM"
	fi
	exit 1
fi

# END - PrepareBuilder
#---------------------

#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# MvnPlugin // ggo
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
verbosePrint "MvnPlugin // ggo"

if [ "$START_GGO" = "YES" ]; then

	#---------------------
	# START - Plugin-PrepareComp

	IFS=',' read -r -a array <<<"$TYPE_SOURCE"
	for typeSourceElement in "${array[@]}"; do
		IFS=: read comp type pathOrVersion <<<"$typeSourceElement"

		if [ "$comp" == "ggo" ]; then
			TYPE_SOURCE_GGO=$type
			if [ "$TYPE_SOURCE_GGO" == "local" ]; then
				TYPE_SOURCE_GGO_PATH=$pathOrVersion
			else
				TYPE_SOURCE_GGO_VERSION=$pathOrVersion
			fi
		fi

	done

	if [ "$TYPE_SOURCE_GGO" == "docker" ]; then
		if [ -z "$TYPE_SOURCE_GGO_VERSION" ]; then
			TYPE_SOURCE_GGO_VERSION=latest
		fi

	fi

	verbosePrint "TYPE_SOURCE_GGO = $TYPE_SOURCE_GGO // TYPE_SOURCE_GGO_PATH = $TYPE_SOURCE_GGO_PATH // TYPE_SOURCE_GGO_VERSION = $TYPE_SOURCE_GGO_VERSION"

	# END - Plugin-PrepareComp
	#---------------------

	if [ "$TYPE_SOURCE_GGO" == "local" ]; then
		f_build() {
			verbosePrint "pwd=$(pwd)\nmvn $MVN_CLEAN $MVN_OPTS package"

			export OPENSSL_CONF=/etc/ssl/
			mvn $MVN_CLEAN $MVN_OPTS package

		}
	fi

	if [ "$TYPE_SOURCE_GGO" == "docker" ]; then

		dockerImage=maven_build
		mkdir -p localrun/dockerbuild
		cat <<-EOFDOCK >localrun/dockerbuild/Dockerfile
			    FROM maven:$TYPE_SOURCE_GGO_VERSION
			    RUN apt-get update && \\ 
			      if [ "\$(cat /etc/*release|grep ^ID=)" = "ID=debian"  ]; then \\
			        if [ "\$(cat /etc/debian_version)" = "8.11" ]; then \\
			           curl -sL https://deb.nodesource.com/setup_6.x | bash -; apt-get -qy install nodejs; \\
			        elif [ "\$(cat /etc/debian_version)" = "9.5" ]; then \\
			          curl -sL https://deb.nodesource.com/setup_6.x | bash -; apt-get -qy install nodejs; \\
			        else curl -sL https://deb.nodesource.com/setup_10.x | bash -; apt-get -qy install nodejs; fi \\
			      elif [ "\$(cat /etc/*release|grep ^ID=)" = "ID=ubuntu"  ]; then \\
			        curl -sL https://deb.nodesource.com/setup_10.x | bash -; apt-get -qy install nodejs; \\
			      else \\
			        echo "only debian or ubuntu are supported."; \\
			        exit 1; \\
			      fi \\
			     && \\
			      apt-get clean && \\
			      rm -rf /tmp/* /var/tmp/* /var/lib/apt/archive/* /var/lib/apt/lists/*
			    RUN npm install -g jasmine
			    ENTRYPOINT ["/usr/local/bin/mvn-entrypoint.sh"]
			    CMD ["mvn"]
		EOFDOCK
		verbosePrint "docker build --tag maven_build:$TYPE_SOURCE_GGO_VERSION localrun/dockerbuild/"
		docker build --tag maven_build:$TYPE_SOURCE_GGO_VERSION localrun/dockerbuild/

		f_build() {
			verbosePrint "pwd=$(pwd)\ndocker run --name=ggo --rm -v $(pwd):/usr/src/build -v "$(pwd)/localrun/.m2":/root/.m2 -w /usr/src/build $dockerImage:$TYPE_SOURCE_GGO_VERSION mvn $MVN_CLEAN $MVN_OPTS package"

			docker run --name=ggo --rm -e OPENSSL_CONF=/etc/ssl/ -v "$(pwd)":/usr/src/build -v "$(pwd)/localrun/.m2":/root/.m2 -w /usr/src/build $dockerImage:$TYPE_SOURCE_GGO_VERSION mvn $MVN_CLEAN $MVN_OPTS package

		}
	fi

	if [ "$SKIP_BUILD" != "YES" ]; then
		if [ -n "$CLEAN" ]; then
			MVN_CLEAN=clean
		fi
		f_build
	else
		verbosePrint "Mvn build skipped."
	fi

fi

#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
# TomcatPlugin // tomcat
#~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
verbosePrint "TomcatPlugin // tomcat"

if [ "$START_TOMCAT" = "YES" ]; then

	#---------------------
	# START - Plugin-PrepareComp

	IFS=',' read -r -a array <<<"$TYPE_SOURCE"
	for typeSourceElement in "${array[@]}"; do
		IFS=: read comp type pathOrVersion <<<"$typeSourceElement"

		if [ "$comp" == "tomcat" ]; then
			TYPE_SOURCE_TOMCAT=$type
			if [ "$TYPE_SOURCE_TOMCAT" == "local" ]; then
				TYPE_SOURCE_TOMCAT_PATH=$pathOrVersion
			else
				TYPE_SOURCE_TOMCAT_VERSION=$pathOrVersion
			fi
		fi

	done

	if [ "$TYPE_SOURCE_TOMCAT" == "docker" ]; then
		if [ -z "$TYPE_SOURCE_TOMCAT_VERSION" ]; then
			TYPE_SOURCE_TOMCAT_VERSION=latest
		fi

	fi

	if [ "$TYPE_SOURCE_TOMCAT" == "download" ]; then
		if [ -z "$TYPE_SOURCE_TOMCAT_VERSION" ]; then
			TYPE_SOURCE_TOMCAT_VERSION=9
		fi
		# find latest tomcat version for $TYPE_SOURCE_TOMCAT_VERSION
		if [ "$(uname)" == "Linux" ]; then
			GREP_PERL_MODE="-P"
		fi
		TOMCAT_BASE_URL="http://mirror.vorboss.net/apache/tomcat"
		TOMCAT_VERSION_PRE=$(curl -s "$TOMCAT_BASE_URL/tomcat-$TYPE_SOURCE_TOMCAT_VERSION/" | grep -m1 -o $GREP_PERL_MODE "<a href=\"v\d*.\d*.\d*" || echo "__________9.0.10")
		TOMCAT_VERSION=${TOMCAT_VERSION_PRE:10}
		TOMCAT_URL=$TOMCAT_BASE_URL/tomcat-$TYPE_SOURCE_TOMCAT_VERSION/v$TOMCAT_VERSION/bin/apache-tomcat-$TOMCAT_VERSION.tar.gz
	fi

	verbosePrint "TYPE_SOURCE_TOMCAT = $TYPE_SOURCE_TOMCAT // TYPE_SOURCE_TOMCAT_PATH = $TYPE_SOURCE_TOMCAT_PATH // TYPE_SOURCE_TOMCAT_VERSION = $TYPE_SOURCE_TOMCAT_VERSION"

	# END - Plugin-PrepareComp
	#---------------------

	if [ "$TYPE_SOURCE_TOMCAT" == "download" ]; then
		if [ -f ".tomcatPid" ] && [ "$(<.tomcatPid)" != "download" ]; then
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
	fi

	dockerAddLibRefs=()
	if [ "$TYPE_SOURCE_TOMCAT" == "docker" ]; then

		mkdir -p localrun/webapps
		targetPath=localrun/webapps/
	fi

	if [ "$TYPE_SOURCE_TOMCAT" == "download" ]; then

		targetPath=localrun/apache-tomcat-$TOMCAT_VERSION/webapps/
	fi

	if [ "$TYPE_SOURCE_TOMCAT" == "local" ]; then
		targetPath=$TYPE_SOURCE_TOMCAT_PATH/webapps/
	fi

	f_deploy() {
		cp web/target/grid.war $targetPath
	}
	f_deploy

	if [ "$TYPE_SOURCE_TOMCAT" == "download" ]; then
		if [ ! -f ".tomcatPid" ]; then

			export JAVA_OPTS="$JAVA_OPTS "
			./localrun/apache-tomcat-$TOMCAT_VERSION/bin/startup.sh
			echo "download" >.tomcatPid
		else
			echo "Reusing already running instance"
		fi
		tailCmd="tail -f ./localrun/apache-tomcat-$TOMCAT_VERSION/logs/catalina.out"
	fi

	if [ "$TYPE_SOURCE_TOMCAT" == "docker" ]; then
		if [ -f ".tomcatPid" ] && [ "$(<.tomcatPid)" == "download" ]; then
			echo "Tomcat running but started from different source type"
			exit 1
		fi
		if [ ! -f ".tomcatPid" ]; then
			startDockerNetwork

			verbosePrint "docker run --rm -d ${dockerAddLibRefs[@]} -p 8080:8080 --net=gridgameonenet --name=tomcat $ADD_HOST_INTERNAL   -v "$(pwd)/localrun/webapps":/usr/local/tomcat/webapps tomcat:$TYPE_SOURCE_TOMCAT_VERSION"
			dockerContainerIDtomcat=$(docker run --rm -d ${dockerAddLibRefs[@]} -p 8080:8080 \
				--net=gridgameonenet --name=tomcat $ADD_HOST_INTERNAL \
				-v \
				"$(pwd)/localrun/webapps":/usr/local/tomcat/webapps tomcat:$TYPE_SOURCE_TOMCAT_VERSION)
			echo "$dockerContainerIDtomcat" >.tomcatPid
		else
			dockerContainerIDtomcat=$(<.tomcatPid)
			echo "Reusing already running instance $dockerContainerIDtomcat"
		fi
		tailCmd="docker logs -f $dockerContainerIDtomcat"
	fi

	if [ "$TYPE_SOURCE_TOMCAT" == "local" ]; then
		if [ -f ".tomcatPid" ]; then
			echo "Tomcat running but started from different source type"
			exit 1
		fi
		tailCmd="tail -f $TYPE_SOURCE_TOMCAT_PATH/logs/catalina.out"
	fi

fi

#---------------------
# START - WaitBuilder

# waiting for ctrl-c
echo "*************************************************************"
echo "**** SCRIPT COMPLETED, STARTUP IN PROGRESS ******************"
if [ "$TAIL" == "YES" ]; then
	echo "http://localhost:8080/grid"
	echo "**** now tailing log: $tailCmd"
	$tailCmd
else
	echo "http://localhost:8080/grid"
	echo "$tailCmd"
	echo "<return> to rebuild, ctrl-c to stop tomcat"
	while true; do
		read </dev/tty
		f_build
		f_deploy
	done
fi

# END - WaitBuilder
#---------------------

