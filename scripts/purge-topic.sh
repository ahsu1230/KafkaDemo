/opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic mmr-user-input --config retention.ms=1000
/opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic mmr-user-output --config retention.ms=1000
/opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic mmr-match-input --config retention.ms=1000
/opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic mmr-match-output --config retention.ms=1000

echo "Waiting for messages to purge..."
sleep 5s

/opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic mmr-user-input --config retention.ms=86400
/opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic mmr-user-output --config retention.ms=86400
/opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic mmr-match-input --config retention.ms=86400
/opt/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --alter --topic mmr-match-output --config retention.ms=86400