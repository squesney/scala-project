import scala.concurrent.{Await, Future, Promise}
import scala.swing
import scala.swing.{Frame, SimpleSwingApplication}
import scala.concurrent.ExecutionContext.Implicits.global

object Main {
    def main(args: Array[String]): Unit = {
        val (air, count, run) = CsvParser.parse(Array("airports.csv", "countries.csv", "runways.csv"))
        MyDB.populate(air, count, run)
    }
}
