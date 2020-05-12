import cats.Monad
import cats.data.NonEmptyList

import scala.concurrent.duration._

final case class Epoch(millis: Long) extends AnyVal {
  def +(d: FiniteDuration): Epoch = Epoch(millis + d.toMillis)

  def -(e: Epoch): FiniteDuration = (millis - e.millis).millis
}

trait Drone[F[_]] {
  def getBacklog: F[Int]

  def getAgents: F[Int]
}

final case class MachineNode(id: String)

trait Machines[F[_]] {
  def getTime: F[Epoch]

  def getManaged: F[NonEmptyList[MachineNode]]

  def getAlive: F[Map[MachineNode, Epoch]]

  def start(node: MachineNode): F[MachineNode]

  def stop(node: MachineNode): F[MachineNode]
}

final case class WorldView(
                            backlog: Int,
                            agents: Int,
                            managed: NonEmptyList[MachineNode],
                            alive: Map[MachineNode, Epoch],
                            pending: Map[MachineNode, Epoch],
                            time: Epoch
                          )

trait DynAgents[F[_]] {
  def initial: F[WorldView]
  def update(old: WorldView): F[WorldView]
  def act(world: WorldView): F[WorldView]
}

final class DynAgentsModule[F[_]: Monad](D: Drone[F], M: Machines[F])
  extends DynAgents[F] {
  override def initial: F[WorldView] = for {
    db <- D.getBacklog
    da <- D.getAgents
    mm <- M.getManaged
    ma <- M.getAlive
    mt <- M.getTime
  } yield WorldView(db, da, mm, ma, Map.empty, mt)

  override def update(old: WorldView): F[WorldView] = for {
    snap <- initial
    changed <- symdiff(old.alive.keySet, snap.alive.keySet)
    pending = (old.pending - changed).filterNot {
      case (_, started) => (snap.time - started) >= 10.minutes
    }
    update = snap.copy(pending = pending)
  } yield update

  private def symdiff[T](a: Set[T], b: Set[T]): Set[T] = (a union b) -- (a intersect b)

  override def act(world: WorldView) = ???
}


/// To be continued