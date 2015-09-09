package models

import scala.collection.mutable.MutableList

case class CharacterSet(key: String, description: String)

object CharacterSet {

  import play.api.db._
  import play.api.Play.current

  def list = {
    val list = MutableList[CharacterSet]()
    DB.withConnection { conn =>
      val stm = conn.createStatement()
      val res = stm.executeQuery("""
      select name, description
      from  shit
      order by name """)
      while (res.next()) {
        list.+=(CharacterSet(res.getString(1), res.getString(2)))
      }
    }
    list
  }
}