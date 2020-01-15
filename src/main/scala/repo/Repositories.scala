package repo

import cats.data.State
import core.domain.{MetricData, Note}
import interpreter.NoteAppDslInMemory.Logger


object TypeAliases {
  type NoteRepo = List[Note]
  type MetricRepo = List[MetricData]
  type LogRepo = Logger

  type MyStateRepo[T] = State[Repositories, T]
}

import TypeAliases._

case class Repositories(noteRepo: NoteRepo, metricRepo: MetricRepo, logRepo: LogRepo = Logger())
