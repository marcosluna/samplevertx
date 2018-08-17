# Sample Vertx.io app

Sample application build with vertx.

This project demostrate the use of Vertx.io to consume and publish websercies, the test is done against a public exchange rate service once registerd.

What you need:
maven, java 1.8+


From the command line go to the project directory


How to build it:

mvn clean install


How to run it:

java -jar target/vertx-start-project-1.0-SNAPSHOT-fat.jar


with the browser, open http://localhost:8080


then try filtering the info with:

http://localhost:8080?currency=USD

you can try differetn currencies.
