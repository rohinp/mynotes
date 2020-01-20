package core.domain

case class Note private(title:String, tags:Set[String], data:String) {
  override def toString: String = s"Note(title = $title, tags = $tags, data = $data)"
}

object Note {
  def apply(title:String, tags:Set[String],data:String):Either[NoteError,Note] =
    if(title.isEmpty) Left(InvalidNoteTitle) else Right(new Note(title,tags,data))
}
