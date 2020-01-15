package interpreter

import cats.data.State
import core.domain.{EmptyNoteRepo, MetricData, MetricError, MetricNotFound, NotFound, Note, NoteAppDsl, NoteCounter, NoteError, TagCounter}

object NoteInterpreter {

  case class Logger() {
    def info: String => Unit = msg => println(Console.YELLOW + "Info: " + msg)

    def error: String => Unit = msg => println(Console.RED + "Error: " + msg)
  }

  type NoteRepo = List[Note]
  type MetricRepo = List[MetricData]
  type LogRepo = Logger

  case class Repositories(noteRepo: NoteRepo, metricRepo: MetricRepo, logRepo: LogRepo = Logger())

  type MyStateRepo[T] = State[Repositories, T]

  implicit object NoteStateInterpreter extends NoteAppDsl[MyStateRepo] {
    override def info: String => State[Repositories, Unit] = msg => State(rs => (rs, rs.logRepo.info(msg)))

    override def error: String => State[Repositories, Unit] = msg => State(rs => (rs, rs.logRepo.error(msg)))

    override def incrementByTitle: String => State[Repositories, Either[MetricError, Unit]] = title => State {
      repos => {
        if (repos.metricRepo.exists({
          case NoteCounter(t, _) => title == t
          case _ => false
        }))
          (repos.copy(metricRepo = repos.metricRepo.map {
            case n@NoteCounter(t, _) if title == t => n.copy(visitCount = n.visitCount + 1)
            case md => md
          }), Right(()))
        else
          (repos, Left(MetricNotFound))
      }
    }

    override def incrementByTag: String => State[Repositories, Either[MetricError, Unit]] = tag => State {
      repos => {
        if (repos.metricRepo.exists({
          case TagCounter(t, _) => tag == t
          case _ => false
        }))
          (repos.copy(metricRepo = repos.metricRepo.map {
            case n@TagCounter(t, _) if tag == t => n.copy(count = n.count + 1)
            case md => md
          }), Right(()))
        else
          (repos, Left(MetricNotFound))
      }
    }

    override def titleCount: String => State[Repositories, Either[MetricError, MetricData]] = title =>
      State.inspect(_.metricRepo.find({
        case NoteCounter(t, _) => title == t
        case _ => false
      }).toRight(MetricNotFound))

    override def tagCount: String => State[Repositories, Either[MetricError, MetricData]] = tag =>
      State.inspect(_.metricRepo.find({
        case TagCounter(t, _) => tag == t
        case _ => false
      }).toRight(MetricNotFound))

    override def listAllNotes: State[Repositories, Either[NoteError, List[Note]]] =
      State.inspect(repos => if (repos.noteRepo.isEmpty) Left(EmptyNoteRepo) else Right(repos.noteRepo))

    override def create: String => Set[String] => String => State[Repositories, Either[NoteError, Note]] =
      title => tags => content => State {
        repos => {
          Note(title, tags, content).fold(
            err => (repos, Left(err)),
            n => (repos.copy(noteRepo = repos.noteRepo ++ List(n)), Right(n))
          )
        }
      }

    override def findByTitle: String => State[Repositories, Either[NoteError, Note]] = title =>
      State.inspect(_.noteRepo.find(_.title == title).toRight(NotFound))

    override def findByTag: String => State[Repositories, Either[NoteError, List[Note]]] = tag =>
      State.inspect(repos => {
        val filterResults = repos.noteRepo.filter(_.tags.contains(tag))
        if (filterResults.nonEmpty) Right(filterResults)
        else Left(NotFound)
      })

    override def modify: String => Set[String] => String => State[Repositories, Either[NoteError, Note]] =
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

    override def delete: String => State[Repositories, Either[NoteError, Unit]] =
      title => State { repos => {
        if (repos.noteRepo.exists(_.title == title))
          (repos.copy(noteRepo = repos.noteRepo.filter(_.title != title)), Right(()))
        else
          (repos, Left(NotFound))
      }
      }
  }
}
