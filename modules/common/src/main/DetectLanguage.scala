package lila.common

import lila.common.PimpedJson._
import lila.common.ws.WS
import play.api.i18n.Lang
import play.api.libs.json._

// http://detectlanguage.com
final class DetectLanguage(url: String, key: String) {

  private case class Detection(
    language: String,
    confidence: Float,
    isReliable: Boolean)

  private implicit val DetectionReads = Json.reads[Detection]

  private val messageMaxLength = 2000

  def apply(message: String): Fu[Option[Lang]] =
    WS.url(url).post(Map(
      "key" -> Seq(key),
      "q" -> Seq(message take messageMaxLength)
    )) map { response =>
      (response.json \ "data" \ "detections").as[List[Detection]]
        .filter(_.isReliable)
        .sortBy(-_.confidence)
        .headOption map (_.language) flatMap Lang.get
    }

}

object DetectLanguage {

  import com.typesafe.config.Config
  def apply(config: Config): DetectLanguage = new DetectLanguage(
    url = config getString "api.url",
    key = config getString "api.key")
}