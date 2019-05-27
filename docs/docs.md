Yirgacheffe is a simple language designed to run on the JVM and is in many ways similar to Java.
While it lacks many of the features of the modern Java language and even some of Java's more traditional ones,
it is still possible to write good object oriented code in Yirgacheffe.
Below are listed some of the features that Yirgacheffe lacks along with explanations of what can be used instead.

### Inheritance

### Public Fields

### Mutable Fields

There's nothing wrong with mutable state, almost all programs have some, but it is important to distinguish between the global state of the program and the local state of a calculation.
One of the easiest ways to degrade the readability and testability of a program is to mutate a field just because it's convenient for the current calculation even though the long term state of the program hasn't changed.
In Yirgacheffe fields are immutable, they can only be assigned in a constructor, so local state like the value of a loop index should be kept in a local variable.

If the state of a programme really changes then that may well involve mutating the state in a field.
Any mutable data structure can be used to do this as long as it is initialised and assigned when its parent object is constructed.
In this way an appropriate data structure can be used to store the long term state of a program while keeping other classes immutable.

### Static Methods

OK this is a bit of an omission.
There are some good uses of static methods such a factory methods, but you can do without them.
There is always a temptation to abuse static members and when they are misused the implications are inherently non-local.
So static methods, as well as non-constant static fields have been left out of Yirgacheffe.

### Type Casting

You cannot cast an object down to it's specific subtype or check an object's subtype with `instanceof`.
This has always been an undesirable thing to do in Java and a source of runtime errors.

Casting can often be avoided by using a generic class.
If you know that the return value of a method will need to be cast down to a subtype then putting that method in a generic class allows the subtype to be declared to the compiler by the client code.

If you really want to write subtype specific code you can use a method overload for that subtype.

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

OK so you really want your code to execute out of order so you can do some sort of asynchronous operation such as making an HTTP call.
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
