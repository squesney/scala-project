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
                DBQueries.query(query(1))
            }
        }
        else if (action.contentEquals("Report"))
        {
            DBQueries.report()
        }
    }
}