package diaryapp.persistence.model

import diaryapp.persistence.model.DbTypes.CollectionRef

case class Account(id: String, email: String, profile: CollectionRef) extends Entity