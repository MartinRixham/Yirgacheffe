#! /usr/bin/env bash

if [ $1 = --run ];then
    java -cp .:$0 $2
elif [ $1 = --repl ];then
    java -cp $0 yirgacheffe.repl.Repl
else
    java -cp $0 yirgacheffe.compiler.Yirgacheffe $*
fi

exit 0

