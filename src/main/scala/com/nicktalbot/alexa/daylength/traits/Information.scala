package com.nicktalbot.alexa.daylength.traits

import com.nicktalbot.alexa.daylength.data.{Location, UserAddress}
import scala.concurrent.Future

trait Information {

  def userAddress(apiEndpoint: String, deviceId: String, consentToken: String): Future[UserAddress]

  def ukPostCodeLocation(postCode: String): Future[Location]

  def usZipCodeLocation(zipCode: String): Future[Location]

  def locationDaylight(location: Location): Future[Int]
}
