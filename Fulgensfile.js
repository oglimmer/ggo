module.exports = {

  config: {
    Name: "GridGameOne",
    Vagrant: {
      Box: 'ubuntu/xenial64',
      Install: 'maven openjdk-8-jdk-headless npm phantomjs docker.io',
      AddInstall: [
        'ln -s /usr/bin/nodejs /usr/bin/node',
        'npm install -g jasmine phantomjs-prebuilt',
      ]
    },
  },

  software: {
    "ggo": {
      Source: "mvn",
      Mvn: {
        BuildDependencies: {
          apt: [ "npm", "phantomjs" ],
          npm: [ "jasmine", "phantomjs-prebuilt" ]
        }
      },
      Artifact: "web/target/grid.war"
    },

    "tomcat": {
      Source: "tomcat",
      Deploy: "ggo"
    }
  }
}
