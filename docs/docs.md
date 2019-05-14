Yirgacheffe is a simple language designed to run on the JVM and is in many ways similar to Java.
It lacks many of the features of the modern Java language and even some of Java's more traditional ones.
However it is still possible to write good object oriented code in Yirgacheffe.
Below are listed some of the features that Yirgacheffe lacks along with explanations of what can be used instead.

### Inheritance

### Public Fields

### Mutable Fields

### Static Methods

OK this is a bit of an omission.
There are some good uses of static methods such a factory methods, but you can do without them.
There is always a temptation to abuse static members and when they are misused the implications are inherently non-local.
So static methods, as well as non-constant static fields have been left out of Yirgacheffe.

### Typecasting

You cannot cast an object down to it's specific subtype or check an object's subtype with `instanceof`.
This has always been an undesirable thing to do in Java and a source of runtime errors.

If you want to write subtype specific code you can use a method overload for that subtype.
Something like this.

    class MyClass
    {
        main method(Array<String> args)
        {
            Object object = "really a string";
            
            // returns "it's a string"
            this.handleObject(object);
        }
        
        private String handleObject(Object object)
        {
            return "just an object";
        }
        
        private String handleObject(String object)
        {
            return "it's a string";
        }
    }

### Null Literals

### Try/Catch Blocks

### Switch Statements

### Lambda Expressions

There are no lambda expressions or first class functions but that doesn't mean you can't pass a callback.
Remember that an object is just a collection of callbacks attached to some data, so if you want to pass a callback just put it on an object.

OK so you really wan't your code of execute out of order so you can do some sort of asynchronous operation such as making an HTTP call.
In this case you can declare a method as parallel and it will be executed on a separate thread.

    class MyClass
    {
        main method(Array<String> args)
        {
            Result result = this.callServer();
        }
        
        parallel public Result callServer()
        {
            // async operation here
        }
    }

### Multiple Number Types

There's only one number type in Yirgacheffe.
Just to keep things simple.
It's called `Num` and its a 64 bit floating point number.
This is a good fit for most numerical data.

For some cases such a loop indices an integer would be better.
Like this.

    class MyClass
    {
        main method(Array<String> args)
        {
            for (Num i = 0; i < args.length(); i++)
            {
                args.get(i);
            }
        }
    }
    
In this case the compiler will optimise `i` to be a 32 bit integer.


### Stream Processing

That's not all.
There are even more features that Yirgacheffe lacks such as wildcard types, checked exceptions and inner classes.
You'll just have to do without them.
It really is possible if you're clever enough.
Have fun!

