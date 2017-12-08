#! /usr/bin/env bash

SOURCE_FILE=$1
CLASS_NAME=$(echo $SOURCE_FILE | cut -f 1 -d '.')
OUTPUT_FILE=$CLASS_NAME".class"

java -jar $0 $SOURCE_FILE > $OUTPUT_FILE

exit 0

