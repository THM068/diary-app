package diaryapp.persistence.model

import faunadb.values.{Native, RefV}

object DbUtil {

  def collectionRef(collectionName: CollectionName, id: CollectionId) = RefV(id.id, RefV(collectionName.name, Native.Collections))

  case class CollectionName(name: String) extends AnyVal
  case class CollectionId(id: String) extends AnyVal
}
