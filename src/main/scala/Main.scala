import MyDB.db
import coursier.sbtcoursier.CoursierPlugin
import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, Future, Promise}
import scala.swing
import scala.swing.{Frame, SimpleSwingApplication, Swing}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.io.StdIn.readLine
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.io.StdIn

object Main {
    def main(args: Array[String]): Unit = {
        if (args.contains("--help"))
        {
            println("use: stb run [--gui]\nA basic query app for an airport database.\n\n--gui for the graphical user interface\n")
        }
        else {
            val (air, count, run) = CsvParser.parse(Array("airports.csv", "countries.csv", "runways.csv"))
            MyDB.populate(air, count, run)
            if (args.contains("--gui"))
            {
                val ui = new UI
                ui.open()
            }
            else
            {
                println("You can get a Report by typing report")
                println("You can get information on a specific airport" +
                    " by typing query followed by country name or code")
                println("You can quit by typing quit")
                read_infos()
                MyDB.close()
            }
        }
    }

    @scala.annotation.tailrec
    def read_infos(): Unit = {
        println("query or report ?")
        val action = readLine().toLowerCase
        if (action.startsWith("query"))
        {
            println("Type a country code or name:")
            val country = readLine().toLowerCase
            DBQueries.print_query(country)
        }
        else if (action.startsWith("report"))
        {
            DBQueries.print_reports()
        }
        if (!action.startsWith("quit"))
            read_infos()
    }
}