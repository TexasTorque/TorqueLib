# My liberal use of `final`

For someone who asks why [I (Justus)](https://justusl.com) use `final` in every possible context.

* To be overly explicit for readability  purposes
* To prevent unwanted mutation

This mostly applies to fields and local variables. Declaring immutability is important.
It lets us know how a value will change over runtime, and how we can avoid side effects.
This is the same reason I use `final` in parameters declarations. If a parameter is *not*
marked `final` in my code it *will* be mutated. To keep this connotation, we must use `final`
everywhere else. I also use `final` when I can to explicitly specify extendibility. This is
important in building a library, like [TorqueLib](https://github.com/TexasTorque/TorqueLib),
and as a result of this, I use it in the entire Texas Torque codebase. The most extraneous
uses is marking methods that are members of a `final` class or marking static methods as `final`.
The reason I do this is just to maintain consistency with all function declarations. This
is the only case in which I think I may overuse `final`, but it's a habit.

## Sources

* [Excessive use "final" keyword in Java (https://softwareengineering.stackexchange.com/a/98703)](https://softwareengineering.stackexchange.com/a/98703)
* [The Final Word On the final Keyword (https://web.archive.org/web/20050212033242/http://renaud.waldura.com/doc/java/final-keyword.shtml#conclusion)](https://web.archive.org/web/20050212033242/http://renaud.waldura.com/doc/java/final-keyword.shtml#conclusion)
* [Use Final Liberally (http://www.javapractices.com/topic/TopicAction.do?Id=23)](http://www.javapractices.com/topic/TopicAction.do?Id=23)
