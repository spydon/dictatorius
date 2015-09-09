package controllers

import play.api.libs.functional.syntax.functionalCanBuildApplicative
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._
import play.api.libs.json.JsPath
import play.api.libs.json.Json.toJson
import play.api.libs.json.Reads
import play.api.libs.json.Reads._
import play.api.libs.json.Reads.StringReads
import play.api.libs.json.Reads.functorReads
import play.api.mvc.Action
import play.api.mvc.Controller
import scala.collection.mutable.MutableList

class Inloggning extends Controller {

  // Shows the login screen and empties the session:
  def login = Action {
    Ok(views.html.login()).withNewSession
  }

  // Handles the username-password sent as JSON:
  def reallogin = Action(parse.json) { request =>

    // Creates a reader for the JSON - turns it into a LoginRequest
    implicit val loginRequest: Reads[LoginRequest] = Json.reads[LoginRequest]

    /*
     * Call validate and if ok we return valid=true and put username in session
     */
    request.body.validate[LoginRequest] match {
      case s: JsSuccess[LoginRequest] if (s.get.authenticate) => {
        Ok(toJson(Map("valid" -> true))).withSession("user" -> s.get.username)
      }
      // Not valid
      case _ => Ok(toJson(Map("valid" -> false)))
    }
  }

  def welcome = Action { implicit request =>
    request.session.get("user").map {
      user =>
        {
          Ok(views.html.welcome(user))
        }
    }.getOrElse(Redirect(routes.Inloggning.login()))
  }
}

case class LoginRequest(username: String, password: String) {

  import play.api.db._
  import play.api.Play.current
   def list = {
    val list = MutableList[String]()
    DB.withConnection { conn =>
      val stm = conn.createStatement()
      val res = stm.executeQuery("""
      select username
      from  userlist
      where username = '""" + username + "' and password = sha2('" + password + "', 256)")
      while (res.next()) {
        list.+=(res.getString(1))
      }
    }
    list
  }

  val authenticate = !list.isEmpty;
}