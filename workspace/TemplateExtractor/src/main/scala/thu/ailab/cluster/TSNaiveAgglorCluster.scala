package thu.ailab.cluster

import scala.collection.mutable.{HashMap => MHashMap}
import thu.ailab.sequence.TagSegment
import thu.ailab.global.MyConfigFactory
import thu.ailab.distance.TSDistance

class TSNaiveAggloCluster(val tss: Array[TagSegment]) extends {
  override val initSize = tss.size
} with NaiveAggloCluster {
  /**
   * overrides
   */
  override val clusterThreshold = MyConfigFactory.getValue[Double](
      "cluster.TSNaiveAggloCluster.clusterThreshold")
  override def initDistArray() = {
    new TSDistance(tss).doCalculation
  }
  override def composeShow(verbose: Boolean) = {
    if (verbose) (id: Int) => tss(id).toString
    else (id: Int) => id.toString
  }
  def outputCluster() = {
    clusters.foreach(x => println(x._1 + "\n" + x._2.toStr(false)))
  }    
}