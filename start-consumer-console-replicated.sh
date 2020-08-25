#!/usr/bin/env bash
cd ~/Downloads/kafka-training

kafka/bin/kafka-console-consumer.sh \
    --bootstrap-server localhost:9094,localhost:9092 \
    --topic my-example-topic \
	--consumer-property group.id=mygroup

