# Creating my first Kafka project

First install Maven (https://www.baeldung.com/install-maven-on-windows-linux-mac)

## Writing in Kafka Streams
https://kafka.apache.org/26/documentation/streams/tutorial

### Starting Way 1: Setup a Maven Project from Kafka Streams Archetype
```
mvn archetype:generate \
            -DarchetypeGroupId=org.apache.kafka \
            -DarchetypeArtifactId=streams-quickstart-java \
            -DarchetypeVersion=2.6.0 \
            -DgroupId=streams.examples \
            -DartifactId=streams.examples \
            -Dversion=0.1 \
            -Dpackage=myapps
```

This will create a basic Java project with a `pom.xml` at the root and some sample files.
Sample files were deleted so we could start from scratch.

### Starting Way 2: Setup a Maven Project from scratch
<https://www.learningjournal.guru/article/kafka/configuring-intellij-idea-for-kafka/>
<https://www.youtube.com/watch?v=REwaZvymBHs>

- Create a new maven project/module and define `groupId` and `artifactId` for your project.
- Include the Maven Compile Plugin under `<build/>`
- Include dependencies under `<dependencies/>`
  - Apache Kafka Clients (Kafka API)
  - Apache Kafka Streams (Kafka API)
  - Apache Log4J2 (Logging)
  - JUnit Jupiter (Junit tests)
- Configure `log4j2.xml` file in `src/resources`

- Create scripts for starting zookeeper, kafka servers, kafka producers & consumers, and creating a kafka topic. Now you can run these scripts in IntelliJ WITHOUT having to go back & forth with Terminal.

- Create Producer java class
- Setup `Run configuration`. Specify main class, directory, program arguments, Java Run Environment (JRE), etc.
- Run Java class and you should see results in Consumer process.
- Awesome it works!
