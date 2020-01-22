package core.service

import core.domain.{NotFound, Note, TagCounter, TitleCounter}
import org.scalatest.WordSpec
import core.service.NoteModule._
import dsl.interpreter.inmemory.NoteAppDslInMemory._
import repo.inmemory.Repositories

class NoteAppSpec extends WordSpec{
  "A Note" can {
    val emptyRepo = Repositories(List(), List(),List())

    "create operations" should {
      "cannot create an invalid note and log an error message" in {
        val program = create("",Set(),"some data11")
        val result = program.run(emptyRepo).value
        assert(result._2 == Note("",Set(),"some data11"))
        assert(result._1.logRepo == List("Info: Note creation failed with error InvalidNoteTitle"))
      }

      "cannot create a note which already exists and log an error message" in {
        val program = for {
          validNote <- create("n1", Set(), "some data11")
          _ <- create("n1", Set(), "some data11")
        } yield validNote

        val result = program.run(emptyRepo).value
        assert(result._2 == Note("n1",Set(),"some data11"))
        assert(result._1.logRepo.head == "Info: Note creation failed with error AlreadyExists")
      }

      "create a note and log a message" in {
        val program = create("n1",Set("tag1"),"some data11")
        val result = program.run(emptyRepo).value
        assert(result._2 == Note("n1",Set("tag1"),"some data11"))
        assert(result._1.logRepo == List("Info: Note created with title n1"))
      }
    }

    "search operations" should {
      val program = for {
        _ <- create("n1",Set("tag1"),"some data11")
        _ <- create("n2",Set("tag2"),"some data12222")
        _ <- create("n3",Set("tag3","tag1"),"some data13333")
        _ <- create("n4",Set("tag3","tag4"),"some data444")
        _ <- create("n5",Set("tag3","tag2"),"some data5555")
      } yield ()

      "search note by title which does not exists, log and no metric data created" in {
        val result = (for {
          _ <- program
          r <- searchNoteByTitle("nnn")
        } yield r).run(emptyRepo).value

        assert(result._2 == Left(NotFound))
        assert(result._1.logRepo.head == "Info: Title search with nnn failed with NotFound")
        assert(result._1.metricRepo == List())
      }

      "search note by title which exists, log and metric data created" in {
        val result = (for {
          _ <- program
          r <- searchNoteByTitle("n2")
        } yield r).run(emptyRepo).value

        assert(result._2 == Note("n2",Set("tag2"),"some data12222"))
        assert(result._1.logRepo.head == "Info: Title searched is n2")
        assert(result._1.metricRepo.head == TitleCounter("n2",1))
      }

      "search note by tag which does not exists, log and no metric data created" in {
        val result = (for {
          _ <- program
          r <- searchNoteByTag("nnn")
        } yield r).run(emptyRepo).value

        assert(result._2 == Left(NotFound))
        assert(result._1.logRepo.head == "Info: Tag search with nnn failed with NotFound")
        assert(result._1.metricRepo == List())
      }

      "search note by tag which exists, log and metric data created" in {
        val result = (for {
          _ <- program
          r <- searchNoteByTag("tag2")
        } yield r).run(emptyRepo).value

        val expected = List(Note("n2",Set("tag2"),"some data12222"),Note("n5",Set("tag3","tag2"),"some data5555")).map(_.right.get)

        assert(result._2 == Right(expected))
        assert(result._1.logRepo.head == "Info: Tag searched is tag2")
        assert(result._1.metricRepo.head == TagCounter("tag2",1))
      }
    }
  }
}
