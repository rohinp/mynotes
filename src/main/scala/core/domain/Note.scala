package core.domain

class Note private(val title:String,val tags:Set[String],val data:String) {
  override def toString: String = s"Note(title = $title, tags = $tags, data = $data)"
}

object Note {
  def apply(title:String, tags:Set[String],data:String):Either[NoteError,Note] =
    if(title.isEmpty) Left(InvalidNoteTitle) else Right(new Note(title,tags,data))
}
