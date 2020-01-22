package dsl

import core.domain.{LogDsl, MetricDsl, NoteDsl}

trait NoteAppDsl[F[_]] extends NoteDsl[F,String, String, String] with LogDsl[F] with MetricDsl[F,String, String]
