# 'Day Length' Alexa Skill

An AWS Lambda based Alexa Skill that tells the user the length of daylight between sunrise and sunset at their registered location.

- Scala 2.12 / SBT 0.13
- AWS Lambda
- Alexa Skill
- Akka HTTP

The Alexa Skill provides a response by:

- Retrieving the (US) Zip or (UK) Post code registered location via the Alexa API
- Converts the location zip/post code to latitude and longitude co-ordinates via a third-party REST API
- Retrieves sunrise/sunset info from another third-party API based on the co-ordinates
- Communicates the result to the user

`sbt assembly` produces a deployable fat jar, containing all dependencies including the Scala runtime.

This can be deployed to AWS Lambda using the Java 8 runtime option.
