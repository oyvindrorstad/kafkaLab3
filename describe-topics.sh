#!/usr/bin/env bash

cd ~/Downloads/kafka-training

# List existing topics
kafka/bin/kafka-topics.sh --describe \
    --topic my-example-topic \
    --zookeeper localhost:2181
