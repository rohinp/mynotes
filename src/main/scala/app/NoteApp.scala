package app
import core.service.NoteModule._
import interpreter.NoteInterpreter._

object NoteApp extends App {

  val noteProgram = for {
    _ <- create("n1",Set("tag1"),"some data11")
    _ <- create("n2",Set("tag2"),"some data12222")
    _ <- create("n3",Set("tag3","tag1"),"some data13333")
    _ <- create("n4",Set("tag3","tag4"),"some data444")
    _ <- create("n5",Set("tag3","tag2"),"some data5555")
    _ <- deleteAndListRemainingNotes("n2")
    _ <- modify("n3",Set(),"modified")
  } yield ()

  println(Console.WHITE + "*" * 50)
  println(Console.GREEN + noteProgram.run(Repositories(List(),List())).value._1.noteRepo.mkString("\n"))
  println(Console.WHITE + "*" * 50)
}
