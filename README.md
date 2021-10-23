# Yirgacheffe [![Build Status](https://travis-ci.com/MartinRixham/Yirgacheffe.svg?branch=master)](https://travis-ci.com/MartinRixham/Yirgacheffe)

![Yirgacheffe](docs/yirgacheffe.png)

### Build

`mvn package` produces the executable `executable/target/yirgacheffe`.

### Run

Compile classes:

    yirgacheffe MyClass.yg AnotherClass.yg

Execute:

    yirgacheffe --run MyClass
    
Repl:

    yirgacheffe --repl