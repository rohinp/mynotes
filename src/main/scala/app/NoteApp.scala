package app
import core.service.NoteModule._
import interpreter.inmemory.NoteAppDslInMemory._
import repo.inmemory
import repo.inmemory.Repositories

object NoteApp extends App {

  val noteProgram = for {
    _ <- create("n1",Set("tag1"),"some data11")
    _ <- create("n2",Set("tag2"),"some data12222")
    _ <- create("n3",Set("tag3","tag1"),"some data13333")
    _ <- create("n4",Set("tag3","tag4"),"some data444")
    _ <- create("n5",Set("tag3","tag2"),"some data5555")
    _ <- searchNoteByTag("tag2")
    _ <- searchNoteByTitle("n3")
    _ <- searchNoteByTitle("n3")
    _ <- deleteAndListRemainingNotes("n2")
    _ <- modify("n3",Set(),"modified")
  } yield ()

  private val inMemoryNoteState = noteProgram.run(inmemory.Repositories(List(), List(),List())).value._1
  println((Console.WHITE + "*" * 25) + "Note Repo" + (Console.WHITE + "*" * 25))
  println(Console.GREEN + inMemoryNoteState.noteRepo.mkString("\n"))
  println((Console.WHITE + "*" * 25) + "Metric Data" + (Console.WHITE + "*" * 25))
  println(Console.GREEN + inMemoryNoteState.metricRepo.mkString("\n"))
  println((Console.WHITE + "*" * 25) + "Logs " + (Console.WHITE + "*" * 25))
  println(Console.GREEN + inMemoryNoteState.logRepo.mkString("\n"))
  println(Console.WHITE + "*" * 50)
}
