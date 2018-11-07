module.exports = {

  config: {
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
      EnvVars: [
        // Jasime fails on maven:3-jdk-11 without this
        "OPENSSL_CONF=/etc/ssl/"
      ],
      Artifact: "web/target/grid.war"
    },

    "tomcat": {
      Source: "tomcat",
      Deploy: "ggo"
    }
  }
}
