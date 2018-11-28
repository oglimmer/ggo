module.exports = {

  config: {
    SchemaVersion: "1.0.0",
    Name: "GridGameOne",
    BuildDependencies: {
      Npm: [ "jasmine" ]
    },
    Vagrant: {
      Box: 'ubuntu/xenial64',
      Install: 'maven openjdk-8-jdk-headless docker.io'
    },
  },

  versions: {
    ggo: {
      TestedWith: "3-jre-11"
    },
    tomcat: {
      Docker: "tomcat9-openjdk11-openj9",
      TestedWith: "9 (slim)"
    }
  },

  software: {
    ggo: {
      Source: "mvn",
      Artifact: "web/target/grid.war"
    },

    tomcat: {
      Source: "tomcat",
      DockerImage: "oglimmer/adoptopenjdk-tomcat",
      // runs with 300M on jdk8 with tomcat:9
      DockerMemory: "70M",
      Deploy: "ggo"
    }
  }
}
