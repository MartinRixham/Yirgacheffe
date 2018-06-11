#! /usr/bin/env bash

if [ $1 = --run ];then
    java -cp .:$0 $2
else
    java -cp $0 yirgacheffe.compiler.Yirgacheffe $1
fi

exit 0

