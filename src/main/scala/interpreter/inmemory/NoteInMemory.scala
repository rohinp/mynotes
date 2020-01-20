package interpreter.inmemory

import cats.data.State
import core.domain._
import repo.inmemory.TypeAliases.MyStateRepo

trait NoteInMemory extends NoteDsl[MyStateRepo,String, String, String]{

  def listAllNotes: MyStateRepo[Either[NoteError, List[Note]]] =
    State.inspect(repos => if (repos.noteRepo.isEmpty) Left(EmptyNoteRepo) else Right(repos.noteRepo))

  def create: String => Set[String] => String => MyStateRepo[Either[NoteError, Note]] =
    title => tags => content => State {
      repos => {
        Note(title, tags, content).fold(
          err => (repos, Left(err)),
          n => (repos.copy(noteRepo = repos.noteRepo ++ List(n)), Right(n))
        )
      }
    }

  def findByTitle: String => MyStateRepo[Either[NoteError, Note]] = title =>
    State.inspect(_.noteRepo.find(_.title == title).toRight(NotFound))

  def findByTag: String => MyStateRepo[Either[NoteError, List[Note]]] = tag =>
    State.inspect(repos => {
      val filterResults = repos.noteRepo.filter(_.tags.contains(tag))
      if (filterResults.nonEmpty) Right(filterResults) else Left(NotFound)
    })

  def modify: String => Set[String] => String => MyStateRepo[Either[NoteError, Note]] =
    title => tags => content => State {
      repos => {
        Note(title, tags, content).fold(
          err => (repos, Left(err)),
          n => (repos.copy(noteRepo = repos.noteRepo.map({
            case x if x.title == title => n
            case y => y
          })), Right(n))
        )
      }
    }

  def delete: String => MyStateRepo[Either[NoteError, Unit]] =
    title => State { repos => {
      if (repos.noteRepo.exists(_.title == title)) (repos.copy(noteRepo = repos.noteRepo.filter(_.title != title)), Right(()))
      else (repos, Left(NotFound))
      }
    }
}
