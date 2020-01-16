package core.service

import cats.Monad
import core.domain._
import cats.implicits._
import NoteMetricModule._

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

  def searchNoteByTitle[F[_]](title:String)(implicit dsl:NoteAppDsl[F], F:Monad[F]):F[Either[NoteError,Note]] =
    for {
      note <- dsl.findByTitle(title)
      _ <- dsl.info(s"title searched is $title")
      _ <- incrementByTitle[F](title)
    } yield note

  def searchNoteByTag[F[_]](tag:String)(implicit dsl:NoteAppDsl[F], F:Monad[F]):F[Either[NoteError,List[Note]]] =
    for {
      notes <- dsl.findByTag(tag)
      _ <- dsl.info(s"tag searched is $tag")
      _ <- incrementByTag[F](tag)
    } yield notes

  def deleteAndListRemainingNotes[F[_]](title:String)(implicit dsl:NoteAppDsl[F], F:Monad[F]):F[Either[NoteError,List[Note]]] =
    for {
      _ <- dsl.delete(title)
      _ <- dsl.info(s"Note with the title $title is deleted")
      notes <- dsl.listAllNotes
    } yield notes

}
