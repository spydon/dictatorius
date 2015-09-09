package controllers
import play.api._
import play.api.mvc._
class About extends Controller {

  def about = Action { implicit request =>
    request.session.get("user").map { user =>
      Ok(views.html.about("Hello", "true"))
  }.getOrElse {
      Ok(views.html.about("Hello", "false"))
  }
  }

}
