package core.service

import cats.Monad
import cats.implicits._
import core.domain.{MetricData, MetricError, NoteError, TagCounter, TitleCounter}
import dsl.NoteAppDsl

object MetricModule {

  def incrementByTitle[F[_]](title:String)(implicit dsl: NoteAppDsl[F], F: Monad[F]):F[Either[MetricError, Unit]] =
    for {
      eitherIs <- dsl.isExists(title)
      r <- eitherIs.fold(
        err => F.pure(Either.left[MetricError, Unit](err)),
        cond => if (cond) dsl.incrementByTitle(title) else dsl.addNewMetric(TitleCounter(title, 1))
      )
    } yield r

  def incrementByTag[F[_]](tag:String)(implicit dsl: NoteAppDsl[F], F: Monad[F]):F[Either[MetricError, Unit]] =
    for {
      eitherIs <- dsl.isExists(tag)
      r <- eitherIs.fold(
        err => F.pure(Either.left[MetricError, Unit](err)),
        cond => if (cond) dsl.incrementByTag(tag) else dsl.addNewMetric(TagCounter(tag, 1))
      )
    } yield r


  def metricData[F[_]](implicit dsl: NoteAppDsl[F], F: Monad[F]): F[Either[NoteError, List[MetricData]]] =
    for {
      notes <- dsl.listAllNotes
      titles = notes.fold(_ => List(), _.map(_.title))
      tags = notes.fold(_ => List(), _.flatMap(_.tags))
      eitherTitles <- metricDataByTitle[F](titles)
      eitherTags <- metricDataByTags[F](tags)
    } yield for {
      mti <- eitherTitles
      mta <- eitherTags
    } yield mti ++ mta

  def sequenceEither: List[Either[MetricError, MetricData]] => Either[MetricError, List[MetricData]] = list => {
    def loop(values: List[Either[MetricError, MetricData]], acc: Either[MetricError, List[MetricData]]): Either[MetricError, List[MetricData]] = values match {
      case Nil => acc
      case Left(err) :: _ => Either.left(err)
      case Right(data) :: xs => loop(xs, acc.map(l => l ++ List(data)))
    }

    loop(list, Either.right(List()))
  }

  def metricDataByTitle[F[_] : Monad](titles: List[String])(implicit dsl: NoteAppDsl[F]): F[Either[MetricError, List[MetricData]]] =
    titles.map(t => dsl.titleCount(t)).sequence.map(sequenceEither)

  def metricDataByTags[F[_] : Monad](tags: List[String])(implicit dsl: NoteAppDsl[F]): F[Either[MetricError, List[MetricData]]] =
    tags.map(t => dsl.tagCount(t)).sequence.map(sequenceEither)
}
