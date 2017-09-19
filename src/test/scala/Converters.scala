import com.amazon.speech.json.SpeechletRequestEnvelope
import com.amazon.speech.slu.Intent
import com.amazon.speech.speechlet.interfaces.system.SystemState
import com.amazon.speech.speechlet._
import com.amazon.speech.ui.{OutputSpeech, PlainTextOutputSpeech, SsmlOutputSpeech}

import scala.language.implicitConversions

object Converters {

  implicit def createRequestEnvelope(name: String): SpeechletRequestEnvelope[IntentRequest] = {

    val device = Device.builder().withDeviceId("TestDevice").build()
    val user = User.builder().withPermissions(new Permissions("ConsentToken")).build()
    val system = SystemState.builder().withDevice(device).withUser(user).build()
    val context = Context.builder().addState(system).build()

    val intent = Intent.builder().withName(name).build()
    val request = IntentRequest.builder().withRequestId("RequestId").withIntent(intent).build()
    val envelope = SpeechletRequestEnvelope.builder().withRequest(request).withContext(context).build()

    envelope
  }

  implicit def getOutputSpeechText(output: OutputSpeech) =

    output match {

      case text: PlainTextOutputSpeech => text.getText
      case ssml: SsmlOutputSpeech => ssml.getSsml
    }
}
