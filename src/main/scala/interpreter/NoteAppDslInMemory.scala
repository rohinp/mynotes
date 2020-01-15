package interpreter

import cats.data.State
import core.domain.NoteAppDsl
import repo.Repositories
import repo.TypeAliases.MyStateRepo

object NoteAppDslInMemory {

  case class Logger() {
    def info: String => Unit = msg => println(Console.YELLOW + "Info: " + msg)
    def error: String => Unit = msg => println(Console.RED + "Error: " + msg)
  }

  implicit object NoteAppDslInMemoryInterpreter extends NoteAppDsl[MyStateRepo] with NoteInMemory with MetricInMemory {
    override def info: String => State[Repositories, Unit] = msg => State(rs => (rs, rs.logRepo.info(msg)))
    override def error: String => State[Repositories, Unit] = msg => State(rs => (rs, rs.logRepo.error(msg)))
  }
}
