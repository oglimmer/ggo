module.exports = {

  config: {
    Name: "GridGameOne",
    Vagrant: {
      Box: 'ubuntu/xenial64',
      Install: 'maven openjdk-8-jdk-headless npm docker.io',
      AddInstall: [
        'ln -s /usr/bin/nodejs /usr/bin/node',
        'npm install -g jasmine',
      ]
    },
  },

  software: {
    "ggo": {
      Source: "mvn",
      Mvn: {
        BuildDependencies: {
          apt: [ "npm" ],
          npm: [ "jasmine" ]
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
