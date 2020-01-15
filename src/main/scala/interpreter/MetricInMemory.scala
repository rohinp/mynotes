package interpreter
import cats.data.State
import core.domain._
import repo.TypeAliases.MyStateRepo

trait MetricInMemory extends MetricDsl[MyStateRepo,String,String] {

  def incrementByTitle: String => MyStateRepo[Either[MetricError, Unit]] = title => State {
    repos => {
      if (repos.metricRepo.exists({
        case TitleCounter(t, _) => title == t
        case _ => false
      }))
        (repos.copy(metricRepo = repos.metricRepo.map {
          case n@TitleCounter(t, _) if title == t => n.copy(visitCount = n.visitCount + 1)
          case md => md
        }), Right(()))
      else
        (repos.copy(metricRepo = repos.metricRepo ++ List(TitleCounter(title,1))), Right(()))
    }
  }

  def incrementByTag: String => MyStateRepo[Either[MetricError, Unit]] = tag => State {
    repos => {
      if (repos.metricRepo.exists({
        case TagCounter(t, _) => tag == t
        case _ => false
      }))
        (repos.copy(metricRepo = repos.metricRepo.map {
          case n@TagCounter(t, _) if tag == t => n.copy(count = n.count + 1)
          case md => md
        }), Right(()))
      else
        (repos.copy(metricRepo = repos.metricRepo ++ List(TagCounter(tag,1))), Right(()))
    }
  }

  def titleCount: String => MyStateRepo[Either[MetricError, MetricData]] = title =>
    State.inspect(_.metricRepo.find({
      case TitleCounter(t, _) => title == t
      case _ => false
    }).toRight(MetricNotFound))

  def tagCount: String => MyStateRepo[Either[MetricError, MetricData]] = tag =>
    State.inspect(_.metricRepo.find({
      case TagCounter(t, _) => tag == t
      case _ => false
    }).toRight(MetricNotFound))
}
