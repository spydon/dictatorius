package models

case class CharacterSetDetail(name: String, defaultCollateName: String, description: String, maxLength: Int)

object CharacterSetDetail {

  import play.api.db._
  import play.api.Play.current

  def get(code: String): Option[CharacterSetDetail] = {
    DB.withConnection { conn =>
      val stm = conn.createStatement()
      val ps = conn.prepareStatement(detailQuery)
      ps.setString(1, code);
      val res = ps.executeQuery()
      while (res.next()) {
        return Some(CharacterSetDetail(res.getString(1), res.getString(2),
          res.getString(3), res.getInt(4)))
      }
    }
    return None
  }

  val detailQuery = """
        select
          character_set_name, default_collate_name,
          description, maxlen
        from character_sets
        where character_set_name = ?"""
}