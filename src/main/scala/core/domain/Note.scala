package core.domain

class Note private(val title:String,val tags:Set[String],val data:String)

object Note {
  def apply(title:String, tags:Set[String],data:String):Either[NoteError,Note] =
    if(title.isEmpty) Left(InvalidNoteTitle) else Right(new Note(title,tags,data))
}
