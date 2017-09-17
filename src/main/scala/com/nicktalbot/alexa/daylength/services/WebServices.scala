package com.nicktalbot.alexa.daylength.services

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.{Authorization, OAuth2BearerToken}
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.{ActorMaterializer, Materializer}
import com.nicktalbot.alexa.daylength.data.{Location, UserAddress}
import com.nicktalbot.alexa.daylength.traits.Information
import org.json4s.native.JsonMethods._
import org.json4s.{DefaultFormats, Formats, JValue}
import org.json4s.native.Serialization.read
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object WebServices {

  private val zipApiKey = "PfcBqgrsikDBi2kIx4tWbyzouh1FQiMGxYiYfKqx438n5t5DyzAJ798Cq9hhs3Y0"
}

class WebServices extends Information {

  private def jsonUnmarshaller[T](transform: String => T) = Unmarshaller.stringUnmarshaller.forContentTypes(MediaTypes.`application/json`).map(transform)
  private def readUnmarshaller[T: Manifest] = jsonUnmarshaller(read[T])

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val materializer: Materializer = ActorMaterializer()
  private implicit val formats: Formats = DefaultFormats

  private implicit val mapUnmarshaller: Unmarshaller[HttpEntity, Map[String, String]] = readUnmarshaller[Map[String, String]]
  private implicit val addressUnmarshaller: Unmarshaller[HttpEntity, UserAddress] = readUnmarshaller[UserAddress]
  private implicit val jValueUnmarshaller: Unmarshaller[HttpEntity, JValue] = jsonUnmarshaller(parse(_, useBigDecimalForDouble = true))

  private val http = Http()

  override def userAddress(apiEndpoint: String, deviceId: String, consentToken: String): Future[UserAddress] =

    makeApiRequest(
      _.to[UserAddress],
      s"$apiEndpoint/v1/devices/$deviceId/settings/address/countryAndPostalCode",
      List(Authorization(OAuth2BearerToken(consentToken)))
    )

  override def ukPostCodeLocation(postCode: String): Future[Location] =

    makeGeoApiRequest(
      s"https://api.postcodes.io/postcodes/${postCode.replace(" ", "")}",
      r => r \ "result",
      "latitude",
      "longitude",
      "admin_district"
    )

  override def usZipCodeLocation(zipCode: String): Future[Location] =

    makeGeoApiRequest(
      s"https://www.zipcodeapi.com/rest/${WebServices.zipApiKey}/info.json/$zipCode/degrees",
      identity,
      "lat",
      "lng",
      "city"
    )

  override def locationDaylight(location: Location): Future[Int] =

    makeApiRequest(
      _.to[JValue],
      s"https://api.sunrise-sunset.org/json?lat=${location.latitude}&lng=${location.longitude}&date=today&formatted=0"
    ).map(_ \ "results" \ "day_length").map(_.extract[Int])


  private def makeApiRequest[T](transform: Unmarshal[HttpEntity] => Future[T], url: String, headers: List[HttpHeader] = List()) =

    http.singleRequest(HttpRequest(HttpMethods.GET, Uri(url), headers)).flatMap {

      case HttpResponse(_, _, entity, _) => transform(Unmarshal(entity))
    }

  private def makeGeoApiRequest(url: String, transform: JValue => JValue, latitude: String, longitude: String, name: String) =

    makeApiRequest(_.to[JValue], url).map(transform).map { result =>

      def value[T: Manifest](field: String) = (result \ field).extract[T]

      Location(value[BigDecimal](latitude), value[BigDecimal](longitude), value[String](name))
    }
}
