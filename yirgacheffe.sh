#! /usr/bin/env bash

SOURCE_FILE=$1
SOURCE_CODE=$(cat $SOURCE_FILE)
CLASS_NAME=$(echo $SOURCE_FILE | cut -f 1 -d '.')
OUTPUT_FILE=$CLASS_NAME".class"

java -jar target/Yirgacheffe.jar "$SOURCE_CODE" > $OUTPUT_FILE
