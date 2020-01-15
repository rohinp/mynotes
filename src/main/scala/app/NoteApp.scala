package app
import core.service.NoteModule._
import interpreter.NoteInterpreter._

object NoteApp extends App {

  val noteProgram = for {
    _ <- create("n1",Set("tag1"),"some data11")
    _ <- create("n2",Set("tag2"),"some data12222")
    _ <- create("n3",Set("tag3","tag1"),"some data13333")
  } yield ()

  val ttt = noteProgram.run(Repositories(List(),List()))

  println(ttt.value._1.noteRepo)
}
