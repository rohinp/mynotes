package core.domain

trait MetricData
case class TitleCounter(title:String, visitCount:Int) extends MetricData
case class TagCounter(tag:String,count:Int) extends MetricData