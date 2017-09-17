package com.nicktalbot.alexa.daylength

import com.amazon.speech.speechlet.Context
import com.amazon.speech.speechlet.interfaces.system.{SystemInterface, SystemState}
import com.amazon.speech.ui._
import scala.collection.JavaConverters._
import scala.language.implicitConversions

object Converters {

  implicit def createPlainSpeech(text: String): PlainTextOutputSpeech = {

    val output = new PlainTextOutputSpeech

    output.setText(text)
    output
  }

  implicit def createSpeechReprompt(speech: OutputSpeech): Reprompt = {

    val reprompt = new Reprompt

    reprompt.setOutputSpeech(speech)
    reprompt
  }

  implicit def createSimpleCard(titleAndContent: (String, String)): SimpleCard = {

    val simpleCard = new SimpleCard
    val (title, content) = titleAndContent

    simpleCard.setTitle(title)
    simpleCard.setContent(content)
    simpleCard
  }

  implicit def createConsentCard(titleAndPermissions: (String, Set[String])): AskForPermissionsConsentCard = {

    val consentCard = new AskForPermissionsConsentCard
    val (title, permissions) = titleAndPermissions

    consentCard.setTitle(title)
    consentCard.setPermissions(permissions.asJava)
    consentCard
  }

  implicit def getSystemState(context: Context): SystemState = context.getState(classOf[SystemInterface], classOf[SystemState])
}
