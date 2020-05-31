import scala.concurrent.{Await, Future, Promise}
import scala.swing
import scala.swing.{Frame, SimpleSwingApplication}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.StdIn.readLine

object Main {
    def main(args: Array[String]): Unit = {
        val (air, count, run) = CsvParser.parse(Array("airports.csv", "countries.csv", "runways.csv"))
        MyDB.populate(air, count, run)

        println("You can get a Report by typing Report")
        println("You can get information on a specific airport" +
          " by typing Query followed by airport name or code")

        val action = readLine()

        if (action.contains(" "))
        {
            val query = action.split(" ")
            if (query(0).contentEquals("Query"))
            {
                println("Call to Query with argument")
            }
        }
        else if (action.contentEquals("Report"))
        {
            println("Call to Report")
        }
    }
}
