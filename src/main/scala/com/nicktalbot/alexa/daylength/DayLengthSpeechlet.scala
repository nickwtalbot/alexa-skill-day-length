package com.nicktalbot.alexa.daylength

import com.amazon.speech.speechlet._
import com.amazon.speech.ui._
import Converters._
import com.amazon.speech.json.SpeechletRequestEnvelope
import com.amazon.speech.speechlet.interfaces.system.SystemState
import com.nicktalbot.alexa.daylength.data.{Location, UserAddress}
import com.nicktalbot.alexa.daylength.traits.Information
import grizzled.slf4j.Logging
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class DayLengthSpeechlet(information: Information) extends SpeechletV2 with Logging {

  private val geocoder: Map[String, String => Future[Location]] = Map(
    "GB" -> information.ukPostCodeLocation,
    "US" -> information.usZipCodeLocation
  )

  private def postalLocation(address: UserAddress) = geocoder(address.countryCode)(address.postalCode)

  override def onIntent(envelope: SpeechletRequestEnvelope[IntentRequest]): SpeechletResponse = {

    (envelope.getRequest.getIntent.getName match {

      case "DayLengthIntent" => tellResponse(envelope.getContext)
      case "AMAZON.HelpIntent" => helpResponse
      case intent if intent == "AMAZON.CancelIntent" || intent == "AMAZON.StopIntent" => "Goodbye"
      case _ => throw new SpeechletException("Invalid Intent")

    }) match {

      case response: SpeechletResponse => response
      case text: String => SpeechletResponse.newTellResponse(text)
    }
  }

  override def onLaunch(envelope: SpeechletRequestEnvelope[LaunchRequest]): SpeechletResponse = helpResponse
  override def onSessionStarted(envelope: SpeechletRequestEnvelope[SessionStartedRequest]) { }
  override def onSessionEnded(envelope: SpeechletRequestEnvelope[SessionEndedRequest]) { }

  private def tellResponse(systemState: SystemState): SpeechletResponse = {

    logger.info(s"Device ID: ${systemState.getDevice.getDeviceId}")

    val consentToken = Option(systemState.getUser).map(_.getPermissions).flatMap(Option(_)).map(_.getConsentToken).flatMap(Option(_))

    if (consentToken.isEmpty) {

       SpeechletResponse.newTellResponse(
         "Day Length requires permission to access your location information. Please enable the settings in the Alexa app",
         ("Permission Required", "Enable Device Country and Location in Settings for this skill")
       )
    } else {

      val address = information.userAddress(systemState.getApiEndpoint, systemState.getDevice.getDeviceId, consentToken.get)
      val location = address.flatMap(postalLocation)
      val daylight = location.flatMap(information.locationDaylight)

      val seconds = Await.result(daylight, Duration.Inf)

      logger.info(s"User Address: ${address.value.flatMap(_.toOption).get}")
      logger.info(s"User Location: ${location.value.flatMap(_.toOption).get}")
      logger.info(s"Daylight: $seconds seconds")

      val duration = Duration(seconds, "seconds")
      val response = s"There are ${duration.toHours} hours, ${duration.toMinutes % 60} minutes, and ${seconds % 60} seconds of daylight today between sunrise and sunset in ${location.value.flatMap(_.toOption).get.name}"

      SpeechletResponse.newTellResponse(response, ("Daylight Period", response))
    }
  }

  private def helpResponse = {

    val output = "Day Length tells you how long daylight lasts between sunrise and sunset at your location"
    val prompt: PlainTextOutputSpeech = "Ask Day Length how long today is"

    SpeechletResponse.newAskResponse(output, prompt, ("Help", "Ask Day Length how long today is"))
  }
}
