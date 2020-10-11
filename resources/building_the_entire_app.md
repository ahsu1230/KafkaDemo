# Building the Matchmaking Application

## Building the front-end

- Create a ReactApp <https://reactjs.org/docs/create-a-new-react-app.html>

## Building the back-end

- Run `MainService.java`

## Architecture

- The MainService is a REST API webserver. It handles a few endpoints for getting a list of all users, matches, creating users, and ending matches.
- Each POST handler creates or updates an entity, so we spin up producers to post to topics.
- Each GET handler retrieves information about entities, so we spin up consumers to consume messages from topics.
- We also have Kafka Streams to listen to "input" topics, process some things, and pipe messages to "output" topics. The Input topics are our Stream "sources" and where we receive messages from producers. The Output topics are our Stream "sinks" and where we consume messages by the Consumers.
- Other relevant classes we needed are Kafka Serializers, Deserializers and Serdes. These are used for type matching and for converting between data types in Kafka topics & streams. These are super important because topic messages are strongly typed.
- Every entity is a POJO and uses Jackson to JSON serialize/deserialize.
- The store (very ghetto atm) is just an in-memory map. For future use, use Kafka CONNECT to dump and read data from actual stores like MySQL. This will really help a lot due to threading issues we were having.

## Resources

- https://docs.confluent.io/current/streams/developer-guide/dsl-api.html#streams-developer-guide-dsl-transformations-stateless
- https://docs.confluent.io/current/streams/developer-guide/dsl-api.html#streams-developer-guide-dsl-transformations-stateful

https://ericlondon.com/2018/07/26/kafka-streams-java-application-to-aggregate-messages-using-a-session-window.html
