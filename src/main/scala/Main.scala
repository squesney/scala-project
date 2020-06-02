import MyDB.db
import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, Future, Promise}
import scala.swing
import scala.swing.{Frame, SimpleSwingApplication}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.io.StdIn.readLine
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.io.StdIn

object Main extends {
    def main(args: Array[String]): Unit = {
        val (air, count, run) = CsvParser.parse(Array("airports.csv", "countries.csv", "runways.csv"))
        MyDB.populate(air, count, run)
        MyDB.print()

        println("You can get a Report by typing report")
        println("You can get information on a specific airport" +
          " by typing query followed by country name or code")
        println("You can quit by typing quit")
        read_infos()
        MyDB.close()
    }

    @scala.annotation.tailrec
    def read_infos(): Unit = {
        println("query or report ?")
        val action = readLine().toLowerCase
        if (action.startsWith("query"))
        {
            println("Type a country code or name:")
            val country = readLine().toLowerCase
            DBQueries.query(country)
        }
        else if (action.startsWith("report"))
        {
            val test = DBQueries.report()
        }
        if (!action.startsWith("quit"))
            read_infos()
    }
}