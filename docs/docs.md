Yirgacheffe is a simple language designed to run on the JVM and is in many ways similar to Java.
While it lacks many of the features of the modern Java language and even some of Java's more traditional ones,
it is still possible to write good object oriented code in Yirgacheffe.
Below are listed some of the features that Yirgacheffe lacks along with suggestions of what can be used instead.

### Inheritance

There is no `extends` keyword in Yirgacheffe so you can't have one class inherit functionality from another.

Use composition instead...
No seriously, readers of your code will be very grateful if you take the trouble to clearly define interfaces between your classes instead of just smooshing them together into one class hierarchy.

OK so you want to write a class that is just like another but with one method implemented differently.
No problem.
You can satisfy an interface by delegating to an implementation of that interface.
The delegate then handles calls to any interface methods not implemented by the delegating class.

    class MyClass implements Comparable<String>
    {
        public MyClass()
        {
            delegate("");
        }
    }

### Public Fields

Hide your data is probably the first rule of object oriented programming so it's nice that this can happen by default.
You may think that having to write accessor methods whenever you want to expose some data is too laborious but it shouldn't be necessary very often.
There's a simple rule to follow.
Put the logic that acts on some data in the class that contains that data.
If you follow it you'll find you hardly ever need to write accessor methods.

### Mutable Fields

There's nothing wrong with mutable state, almost all programs have some, but it is important to distinguish between the global state of the program and the local state of a calculation.
One of the easiest ways to degrade the readability and testability of a program is to mutate a field just because it's convenient for the current calculation even though the long term state of the program hasn't changed.
In Yirgacheffe fields are immutable, they can only be assigned in a constructor or field initialiser, so local state like the value of a loop index should be kept in a local variable.

There is relatively little cost to mutating local state since those mutations cannot propagate to the rest of the program but if the state of a program really changes then that may involve mutating the state in a field.
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

We all know not to pass null values between functions and in Yirgacheffe this is especially important since passing `null` as an argument will result in a null pointer exception.
But really there's not much reason to refer to `null` at all and since I'm trying to encourage myself to use better ways of handling exceptional cases there isn't a null literal in Yirgacheffe.
We can't outlaw null values completely though or pretend they don't exist so you might need to check for nulls to avoid a null pointer exception.

    class MyClass
    {
        main method(Array<String> args)
        {
            // null string
            String nullString = new MutableReference<String>().get();

            if (nullString)
            {
                // this won't be executed
            }
        }
    }

Null values can be handled using conditional statements like this.

### Try/Catch Blocks

Exceptional cases are handled a bit differently than in Java.
Instead of throwing an exception when execution cannot continue you can return a exceptional value from any method.

    public Num getNumber()
    {
        return new Exception();
    }

This exception will propogate through the program and if it isn't handled will end up being printed to standard error.
Any expression can potentially evaluate to an exceptional value.
Exceptional values can be detected using the `try` keyword and handled by calling a method with an overload that handles that exception.

    class MyClass
    {
        main method(Array<String> args)
        {
            this.handle(try this.getNumber());
        }

        public Num getNumber()
        {
            return new Exception();
        }

        public Void handle(Num number)
        {
            // Handle successful case.
        }

        public Void handle(Exception e)
        {
            // Handle exceptional case.
        }
    }

### While, Break and Continue

Loops are a common source of bugs and unnecessary complexity.
I've always tried to keep it simple and avoid using while, break and continue.
Here are the alternatives.

If you don't know beforehand when a loop will terminate then a while loop is one possibility but it is usually less clear than a recursive function.
Recursive functions have to explicitly state their exit conditions instead of updating and checking a boolean.
If a recursive function is tail call optimisable then Yirgacheffe will apply the optimisation at compile time so you don't need to worry about stack overflow exceptions.

Break is easy to avoid. Just put your loop inside a method then `return` can be used to exit the loop early.

Avoiding continue is usually just a case of changing some conditions around.
It's a bit case specific but I've never seen a case where continue is unavoidable.

### Switch Statements

The secret to writing good object oriented code is to minimise branching (now you know) so the temptation to leave out a language feature designed to enable branching is irresistible.
The right way to handle cases with a finite number of values is with enumerations.
In Yirgacheffe enumerations are just a little different than in Java.

    class MyNumeration enumerates Bool
    {
        String message;
    
        true:("The truth!");
    
        false:("Lies!");
    
        MyNumeration(String message)
        {
            this.message = message;
        }
    
        public String getMessage()
        {
            return this.message;
        }
    }
<!-- break -->

    class MyClass
    {
        main method(Array<String> args)
        {
            MyNumeration myEnum = MyNumeration:true;
    
            new System().getOut().println(myEnum.getMessage());
        }
    }

### Lambda Expressions

There are no lambda expressions or first class functions but that doesn't mean you can't pass a callback.
Remember that an object is just a collection of callbacks attached to some data, so if you want to pass a callback just put it on an object.

OK so you really want your code to execute out of order so you can do an asynchronous operation such as making an HTTP call.
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

For some cases such as loop indices an integer would be better.

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

