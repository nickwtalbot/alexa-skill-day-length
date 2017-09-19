import com.nicktalbot.alexa.daylength.DayLengthSpeechlet
import com.nicktalbot.alexa.daylength.data.{Location, UserAddress}
import com.nicktalbot.alexa.daylength.traits.Information
import Converters._
import org.scalatest.{FunSpec, GivenWhenThen, Matchers}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class DayLengthSpec extends FunSpec with GivenWhenThen with Matchers with Information {

  describe("Intent Handling") {

    it("Help Intent returns a help response") {

      Given("A DayLengthSpeechlet")
      val speechlet = new DayLengthSpeechlet(this)

      When("Help Intent")
      val response = speechlet.onIntent("AMAZON.HelpIntent")

      Then("response contains help")
      val text: String = response.getOutputSpeech
      text should include ("tells you how long daylight lasts")
    }

    it("DayLengthIntent on UK address invokes UK Post code check") {

      Given("A DayLengthSpeechlet")
      val speechlet = new DayLengthSpeechlet(this)

      When("Day Length Intent")
      val response = speechlet.onIntent("DayLengthIntent")

      Then("Response contains Location and time")
      val text: String = response.getOutputSpeech
      text should include ("UK")
      text should include ("hours")
    }
  }

  override def userAddress(apiEndpoint: String, deviceId: String, consentToken: String) = Future {

    UserAddress("GB", "AB1 2CD")
  }

  override def ukPostCodeLocation(postCode: String): Future[Location] = Future {

    Location(1.2, 3.4, "UK Test Location")
  }

  override def usZipCodeLocation(zipCode: String): Future[Location] = Future {

    Location(5.6, 7.8, "US Test Location")
  }

  override def locationDaylight(location: Location): Future[Int] = Future { 12345 }
}
