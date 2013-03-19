package lila.wiki

import lila.common.ConfigSettings
import com.typesafe.config.Config

private[wiki] final class Settings(config: Config) extends ConfigSettings(config) {

  val CollectionPage = getString("collection.page")
  val GitUrl = getString("git.url")
}
