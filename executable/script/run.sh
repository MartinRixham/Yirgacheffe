#! /usr/bin/env bash

if [ "$1" == --run ];then
    shift 1
    java -cp .:$0 $*
elif [ "$1" == --repl ];then
    java -cp $0 yirgacheffe.repl.Repl
else
    java -cp $0 yirgacheffe.compiler.Yirgacheffe $*
fi

exit 0

