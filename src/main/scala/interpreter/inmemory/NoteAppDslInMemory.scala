package interpreter.inmemory

import core.domain.NoteAppDsl
import repo.inmemory.TypeAliases.MyStateRepo

object NoteAppDslInMemory {
  implicit object NoteAppDslInMemoryInterpreter extends NoteAppDsl[MyStateRepo] with NoteInMemory with MetricInMemory with LoggerInMemory
}
