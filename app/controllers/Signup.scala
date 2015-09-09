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

class Signup extends Controller {

  // Shows the login screen and empties the session:
  def signup = Action {
    Ok(views.html.signup()).withNewSession
  }

  // Handles the username-password sent as JSON:
  def createUser= Action(parse.json) { request =>

    // Creates a reader for the JSON - turns it into a LoginRequest
    implicit val signupRequest: Reads[signupRequest] = Json.reads[signupRequest]

    /*
     * Call validate and if ok we return valid=true and put username in session
     */
    request.body.validate[signupRequest] match {
      case s: JsSuccess[signupRequest] if (s.get.authenticate) => {
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

case class signupRequest(username: String, password: String) {

  import play.api.db._
  import play.api.Play.current
  def list = {
    val list = MutableList[String]()
    DB.withConnection { conn =>
      val stm = conn.createStatement()
      val res = stm.execute("""
      insert into `userlist` (`username`, `password`)
      values ('"""+username+"', sha2('"+password + "',256));"
      )
      }
      true
    }
  val authenticate = list;
}