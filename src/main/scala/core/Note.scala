package core

case class Note private(title:String, tags:Set[String],data:String)

object Note {
  def apply(title:String, tags:Set[String],data:String):Either[NoteError,Note] =
    if(title.isEmpty()) Left(InvalidNoteTitle) else Right(Note(title,tags,data))
}
