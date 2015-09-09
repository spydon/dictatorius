package controllers

import play.api._
import play.api.mvc._

class Home extends Controller {
 def welcome = Action { implicit request =>
    request.session.get("user").map { user =>
      Ok(views.html.home("Hello", "true"))
  }.getOrElse {
      Ok(views.html.home("Hello", "false"))
  }
  }

  def handleForm = Action(parse.tolerantFormUrlEncoded) { implicit request =>
    val username = request.body.get("username").map(_.head).getOrElse("");
    Ok("Username was " + username)
  }

}
