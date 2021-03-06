package controllers

import lila.app._
import lila.security.Permission
import lila.user.UserRepo
import views._

import play.api.mvc._
import play.api.mvc.Results._

object Mod extends LilaController {

  private def modApi = Env.mod.api
  private def modLogApi = Env.mod.logApi

  def engine(username: String) = Secure(_.MarkEngine) { _ =>
    me => modApi.adjust(me.id, username) inject redirect(username)
  }

  def troll(username: String) = Secure(_.MarkTroll) { _ =>
    me =>
      modApi.troll(me.id, username) inject redirect(username)
  }

  def ban(username: String) = Secure(_.IpBan) { implicit ctx =>
    me => modApi.ban(me.id, username) inject redirect(username)
  }

  def ipban(ip: String) = Secure(_.IpBan) { implicit ctx =>
    me => modApi.ipban(me.id, ip)
  }

  def closeAccount(username: String) = Secure(_.CloseAccount) { implicit ctx =>
    me => modApi.closeAccount(me.id, username) inject redirect(username)
  }

  def reopenAccount(username: String) = Secure(_.ReopenAccount) { implicit ctx =>
    me => modApi.reopenAccount(me.id, username) inject redirect(username)
  }

  def setTitle(username: String) = AuthBody { implicit ctx =>
    me =>
      implicit def req = ctx.body
      if (isGranted(_.SetTitle))
        lila.user.DataForm.title.bindFromRequest.fold(
          err => fuccess(Redirect(routes.User.show(username))),
          title => modApi.setTitle(me.id, username, title) inject redirect(username, false)
        )
      else fuccess(authorizationFailed(ctx.req))
  }

  def log = Auth { implicit ctx =>
    me => modLogApi.recent map { html.mod.log(_) }
  }

  def redirect(username: String, mod: Boolean = true) = 
    Redirect(routes.User.show(username).url + mod.??("?mod"))
}
