package core.domain

sealed trait NoteError

case object NotFound extends NoteError
case object EmptyNoteRepo extends NoteError
case object AlreadyExists extends NoteError
case object InvalidNoteTitle extends NoteError

sealed trait MetricError extends NoteError
case object MetricNotFound extends MetricError