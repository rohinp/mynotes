package dsl.interpreter.inmemory

import cats.data.State
import core.domain._
import repo.inmemory.TypeAliases.MyStateRepo

trait MetricInMemory extends MetricDsl[MyStateRepo, String, String] {

  def isExists: String => MyStateRepo[Either[MetricError, Boolean]] =
    key => State.inspect(repo => Right(repo.metricRepo.exists {
      case TitleCounter(title, _) if key == title => true
      case TagCounter(tag, _) if key == tag => true
      case _ => false
    }))

  override def addNewMetric: MetricData => MyStateRepo[Either[MetricError, Unit]] =
    md => State(repo => (repo.copy(metricRepo = md :: repo.metricRepo), Right(())))

  def incrementByTitle: String => MyStateRepo[Either[MetricError, Unit]] = title => State {
    repos =>
      (repos.copy(metricRepo = repos.metricRepo.map {
        case n@TitleCounter(t, _) if title == t => n.copy(visitCount = n.visitCount + 1)
        case md => md
      }), Right(()))
  }

  def incrementByTag: String => MyStateRepo[Either[MetricError, Unit]] = tag => State {
    repos => {
      (repos.copy(metricRepo = repos.metricRepo.map {
        case n@TagCounter(t, _) if tag == t => n.copy(count = n.count + 1)
        case md => md
      }), Right(()))
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
