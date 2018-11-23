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
      Deploy: "ggo"
    }
  }
}
