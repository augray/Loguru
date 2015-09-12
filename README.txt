Setting up Loguru
=================
For the Java tech newbie- assumes you know the basics of Java but aren't familiar with some of the more periphereal tech like JavaFX and Maven.

Just want to run
---------------- 
1. Ensure you can run JavaFX: Make sure that you have a JVM which has JavaFX packaged with it (Oracle's 1.8 does). You could also try running another JVM, but installing JavaFX like is described here: https://docs.oracle.com/javafx/2/installation/jfxpub-installation.htm#CHDDGGEE . That option is untested though.

2. Download one of the jar files in the distro directory.

2. Run the jars in the distro directory. From the command line java -jar Loguru-Main-0.0.1-SNAPSHOT.jar. Some OSs will let you just double click the jar to run.


To build & develop
------------------
1. Install git: https://git-scm.com/book/en/v2/Getting-Started-Installing-Git

2. Download this repo: You can just clone (https://help.github.com/articles/cloning-a-repository/) if all you want to do is mess around with the code. Fork it first (https://help.github.com/articles/fork-a-repo/) if you want to contribute. Fair warning: unless I know you I may not be very likely to accept your changes- I'd rather not spend much time reviewing other people's code, and I want to keep this project somewhat pristine ;-) .

3. Install Apache Maven: https://maven.apache.org/install.html

4. Import the project rooted in this directory as a Maven project using your favorite Maven compatible ide (Eclipse Luna and newer, NetBeans IDE 6.7 and newer, recent (maybe all?) versions of IntelliJ). If you're oldschool, vi/emacs/your favorite text editor and mvn on the command line ;-) . I'm using Eclipse, and will keep Eclipse-related project files checked in here. These should be pretty unneccesary though as the Maven build file (pom.xml) should pretty much take care of defining everything to build and run.

5. Test out building and running LoguruMainWindow to confirm you have it all set up.

6. Let me know if you have trouble, or give a push request for this file if you think you have helpful tips that should be added here.

7. Check out and work on the dev branch if you want to contribute ("git checkout dev" on the command line from within the project directory)

8. Enjoy coding! This project is for fun, as well as to give friends a project to play with to learn some Java stuff. I hope you learn something!

Helpful Tools
-------------

git: If you're unfamiliar with git, you should learn it! This isn't just a Java wave- if you want to get into software at all, it's THE big thing in version control.
https://git-scm.com/book/en/v2/Getting-Started-About-Version-Control

JavaFX: The UI for Loguru is done in JavaFX. JavaFX is a really cool new addition to the Java language (it's built into the newest versions of the JDK), and will likely become the alternate for Swing.
http://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm#JFXST784

Lambdas: If you're not a Java developer (and even if you are one but don't keep up very well with the language changes), you may be unaware of the addition of Lambdas to Java 8. If you're from another language, you may be familiar with the terminology "lambda", with the similar "functor" design pattern, or with the related functional programming paradigm. Really useful stuff! I make use of Lambdas in Loguru. Here's a tutorial for Java's implementation (assumes no familiarity with the above list of related terms, but does assume familiarity with "anonymous classes", so you might find it helpful to read about those first):
https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html 

Maven: A super sweet Java build tool which tracks dependencies and downloads them for you. It's pretty darn legit, and makes tracking dependencies a breeze. Plus, it's been so popular that most well-known Java IDEs are now providing native Maven integration- so its easy for developers to use their favorite IDE on the same codebase without much extra set up. If you're on the command line and not concerned with changing dependencies, all you really need to know about Maven to build Loguru is to run "mvn clean install" from the command line and run the jar it puts in the target directory. But it's good to know all the same.
https://maven.apache.org/ 

