package core.domain

sealed trait NoteError
case object NotFound extends NoteError
case object EmptyNoteRepo extends NoteError
case object AlreadyExists extends NoteError
case object InvalidNoteTitle extends NoteError

trait NoteDsl[F[_], Title, Tag, Content] {
  def listAllNotes:F[Either[NoteError,List[Note]]]
  def create:Title => Set[Tag] => Content => F[Either[NoteError,Note]]
  def findByTitle:Title => F[Either[NoteError,Note]]
  def findByTag:Tag => F[Either[NoteError,List[Note]]]
  def modify:Title => Set[Tag] => Content => F[Either[NoteError,Note]]
  def delete:Title => F[Either[NoteError,Unit]]
}

trait LogDsl[F[_]] {
  def info:String => F[Unit]
  def error:String => F[Unit]
}

trait MetricData
case class NoteCounter(title:String,visitCount:Int) extends MetricData
case class TagCounter(tag:String,count:Int) extends MetricData

sealed trait MetricError extends NoteError
case object MetricNotFound extends MetricError

trait MetricDsl[F[_], Title, Tag] {
  def incrementByTitle:Title => F[Either[MetricError,Unit]]
  def incrementByTag:Tag => F[Either[MetricError, Unit]]
  def titleCount:Title => F[Either[MetricError,MetricData]]
  def tagCount:Tag => F[Either[MetricError,MetricData]]
}

trait NoteAppDsl[F[_]] extends NoteDsl[F,String, String, String] with LogDsl[F] with MetricDsl[F,String, String]
