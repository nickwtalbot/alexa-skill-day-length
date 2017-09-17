package com.nicktalbot.alexa.daylength

import com.amazon.speech.speechlet.SpeechletV2
import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler
import com.nicktalbot.alexa.daylength.services.WebServices
import scala.collection.JavaConverters._

object DayLengthSpeechletRequestStreamHandler {

  private val applicationIds = Set("amzn1.ask.skill.d8a5380f-358f-49ce-bdc0-6d9012922162")
}

class DayLengthSpeechletRequestStreamHandler(speechlet: SpeechletV2, applicationIds: java.util.Set[String]) extends SpeechletRequestStreamHandler(speechlet, applicationIds) {

  def this() = this(new DayLengthSpeechlet(new WebServices), DayLengthSpeechletRequestStreamHandler.applicationIds.asJava)
}
