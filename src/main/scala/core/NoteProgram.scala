package core

import cats._
import cats.data._
import cats.implicits._

object NoteProgram {

  def isNoteExists[F[_]:Monad](title:String)(implicit dsl:NoteDsl[F]): F[Boolean] =
    dsl.findByTitle(title).map(_.fold(_ => false, n => true))

  def create[F[_]](title:String,content:String)(implicit dsl:NoteDsl[F], F:Monad[F]): F[Either[NoteError,Note]] =
    for {
      check <- isNoteExists[F](title)
      r <- if(check) F.pure(Left(AlreadyExists)) else dsl.create(title)(content)
      _ <- dsl.info(s"Note created with title $title")
    } yield r

  def searchNoteByTitle[F[_]:Monad](title:String)(implicit dsl:NoteDsl[F]):F[Either[NoteError,Note]] =
    for {
      note <- dsl.findByTitle(title)
      _ <- dsl.info(s"title searched is $title")
      _ <- dsl.incrementByTitle(title)
    } yield note

  def searchNoteByTag[F[_]:Monad](tag:String)(implicit dsl:NoteDsl[F]):F[Either[NoteError,List[Note]]] =
    for {
      notes <- dsl.findByTag(tag)
      _ <- dsl.info(s"tag searched is $tag")
      _ <- dsl.incrementByTag(tag)
    } yield notes

  def deleteAndListRemainigNotes[F[_]:Monad](title:String)(implicit dsl:NoteDsl[F]):F[Either[NoteError,List[Note]]] =
    for {
      _ <- dsl.delete(title)
      _ <- dsl.info(s"Note with the title $title is deleted")
      notes <- dsl.listAllNotes
    } yield notes

  def metricData[F[_]:Monad](implicit dsl:NoteDsl[F]):F[Either[NoteError,List[MetricData]]] =
    for {
      notes <- dsl.listAllNotes
      titles = notes.map(t => t.title)
    } yield ???

  def metricDataByTitle[F[_]:Monad](titles:List[String])(implicit dsl:NoteDsl[F]):F[List[Either[MetricError, MetricData]]] =
    titles.map(t => dsl.titleCount(t)).sequence

  def metricDataByTags[F[_]:Monad](tags:List[String])(implicit dsl:NoteDsl[F]):F[List[Either[MetricError, MetricData]]] =
    tags.map(t => dsl.tagCount(t)).sequence

  /*for {
      tm:List[Either[MetricError, MetricData]] <- titles.map(t => dsl.titleCount(t)).sequence
    } yield tm.sequence
   */


  //def loadNotesApp[F[_]:Monad]

}
