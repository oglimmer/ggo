module.exports = {

  config: {
    SchemaVersion: "1.0.0",
    Name: "GridGameOne",
    Vagrant: {
      Box: 'ubuntu/xenial64',
      Install: 'maven openjdk-8-jdk-headless npm docker.io',
      AfterInstall: [
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
          Apt: [ "npm" ],
          Npm: [ "jasmine" ]
        }
      },
      Artifact: "web/target/grid.war",
      EnvVars: [
        "OPENSSL_CONF=/etc/ssl/"
      ]
    },

    "tomcat": {
      Source: "tomcat",
      Deploy: "ggo"
    }
  }
}
