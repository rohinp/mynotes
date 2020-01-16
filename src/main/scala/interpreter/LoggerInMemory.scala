package interpreter

import cats.data.State
import core.domain.LogDsl
import repo.Repositories
import repo.TypeAliases.MyStateRepo

trait LoggerInMemory extends LogDsl[MyStateRepo]{
  override def info: String => State[Repositories, Unit] = msg => State(rs => (rs.copy(logRepo = ("Info: " + msg) :: rs.logRepo),()))
  override def error: String => State[Repositories, Unit] = msg => State(rs => (rs.copy(logRepo = ("Error: " + msg) :: rs.logRepo),()))
}
