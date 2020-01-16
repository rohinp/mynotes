package repo

import cats.data.State
import core.domain.{MetricData, Note}


object TypeAliases {
  type NoteRepo = List[Note]
  type MetricRepo = List[MetricData]
  type LogRepo = List[String]

  type MyStateRepo[T] = State[Repositories, T]
}

import repo.TypeAliases._

case class Repositories(noteRepo: NoteRepo, metricRepo: MetricRepo, logRepo: LogRepo)
