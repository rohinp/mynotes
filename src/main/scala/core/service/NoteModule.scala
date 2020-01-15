package core.service

import cats.Monad
import core.domain._
import cats.implicits._

object NoteModule {

  def isNoteExists[F[_]:Monad](title:String)(implicit dsl:NoteAppDsl[F]): F[Boolean] =
    dsl.findByTitle(title).map(_.fold(_ => false, _ => true))

  def create[F[_]](title:String,tag:Set[String], content:String)(implicit dsl:NoteAppDsl[F], F:Monad[F]): F[Either[NoteError,Note]] =
    for {
      check <- isNoteExists[F](title)
      r <- if(check) F.pure(Left(AlreadyExists):Either[NoteError, Note]) else dsl.create(title)(tag)(content)
      _ <- dsl.info(s"Note created with title $title")
    } yield r

  def modify[F[_]](title:String,tag:Set[String], content:String)(implicit dsl:NoteAppDsl[F], F:Monad[F]): F[Either[NoteError,Note]] =
    for {
      check <- isNoteExists[F](title)
      r <- if(!check) F.pure(Left(NotFound):Either[NoteError, Note]) else dsl.modify(title)(tag)(content)
      _ <- dsl.info(s"Note modified with title $title")
    } yield r

  def searchNoteByTitle[F[_]:Monad](title:String)(implicit dsl:NoteAppDsl[F]):F[Either[NoteError,Note]] =
    for {
      note <- dsl.findByTitle(title)
      _ <- dsl.info(s"title searched is $title")
      _ <- dsl.incrementByTitle(title)
    } yield note

  def searchNoteByTag[F[_]:Monad](tag:String)(implicit dsl:NoteAppDsl[F]):F[Either[NoteError,List[Note]]] =
    for {
      notes <- dsl.findByTag(tag)
      _ <- dsl.info(s"tag searched is $tag")
      _ <- dsl.incrementByTag(tag)
    } yield notes

  def deleteAndListRemainingNotes[F[_]](title:String)(implicit dsl:NoteAppDsl[F], F:Monad[F]):F[Either[NoteError,List[Note]]] =
    for {
      _ <- dsl.delete(title)
      _ <- dsl.info(s"Note with the title $title is deleted")
      notes <- dsl.listAllNotes
    } yield notes

  def metricData[F[_]:Monad](implicit dsl:NoteAppDsl[F]):F[Either[NoteError,List[MetricData]]] =
    for {
      notes <- dsl.listAllNotes
      titles = notes.fold(_ => List(), _.map(_.title))
      tags = notes.fold(_ => List(),_.flatMap(_.tags))
      eitherTitles <- metricDataByTitle[F](titles)
      eitherTags <- metricDataByTags[F](tags)
    } yield for {
      mti <- eitherTitles
      mta <- eitherTags
    } yield mti ++ mta

  def sequenceEither:List[Either[MetricError,MetricData]] => Either[MetricError,List[MetricData]] = list => {
    def loop(values:List[Either[MetricError,MetricData]], acc: Either[MetricError,List[MetricData]]):Either[MetricError,List[MetricData]] = values match {
      case Nil => acc
      case Left(err)::_ => Either.left(err)
      case Right(data)::xs => loop(xs,acc.map(l => l ++ List(data)))
    }
    loop(list,Either.right(List()))
  }

  def metricDataByTitle[F[_]:Monad](titles:List[String])(implicit dsl:NoteAppDsl[F]): F[Either[MetricError, List[MetricData]]] =
      titles.map(t => dsl.titleCount(t)).sequence.map(sequenceEither)

  def metricDataByTags[F[_]:Monad](tags:List[String])(implicit dsl:NoteAppDsl[F]): F[Either[MetricError, List[MetricData]]] =
    tags.map(t => dsl.tagCount(t)).sequence.map(sequenceEither)

}
