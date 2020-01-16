package interpreter

import core.domain.NoteAppDsl
import repo.TypeAliases.MyStateRepo

object NoteAppDslInMemory {
  implicit object NoteAppDslInMemoryInterpreter extends NoteAppDsl[MyStateRepo] with NoteInMemory with MetricInMemory with LoggerInMemory
}
