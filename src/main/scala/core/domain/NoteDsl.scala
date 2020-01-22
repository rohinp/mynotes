package core.domain

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

trait MetricDsl[F[_], Title, Tag] {
  def isExists:String => F[Either[MetricError,Boolean]]
  def addNewMetric:MetricData => F[Either[MetricError,Unit]]
  def incrementByTitle:Title => F[Either[MetricError,Unit]]
  def incrementByTag:Tag => F[Either[MetricError, Unit]]
  def titleCount:Title => F[Either[MetricError,MetricData]]
  def tagCount:Tag => F[Either[MetricError,MetricData]]
}


