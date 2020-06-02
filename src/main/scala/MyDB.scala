
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.collection.parallel.immutable._
import scala.collection.parallel.CollectionConverters._

object MyDB {
    val db = Database.forURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        driver="org.h2.Driver")

    class Countries(tag: Tag) extends Table[(Int, String, String, String)](tag, "COUNTRIES")
    {
        def id = column[Int]("ID", O.PrimaryKey)
        def code = column[String]("CODE")
        def name = column[String]("NAME")
        def continent = column[String]("CONTINENT")
        def * = (id, code, name, continent)
    }

    val countries = TableQuery[Countries]
    class Airports(tag: Tag) extends Table[(Int, String, String, String, String, String, String)](tag, "AIRPORTS")
    {
        def id = column[Int]("ID", O.PrimaryKey)
        def ident = column[String]("IDENT")
        def name = column[String]("NAME")
        def continent = column[String]("CONTINENT")
        def country_ident = column[String]("COUNTRY")
        def region = column[String]("REGION")
        def municipality = column[String]("MUNICIPALITY")
        def * = (id, ident, name, continent, country_ident, region, municipality)
    }
    val airports = TableQuery[Airports]

    class Runways(tag: Tag) extends Table[(Int, Int, String, String, String)](tag, "RUNWAYS")
    {
        def id = column[Int]("ID", O.PrimaryKey)
        def airport_id = column[Int]("AIRPORT_ID")
        def airport_ident = column[String]("AIRPORT_IDENT")
        def surface = column[String]("SURFACE")
        def le_ident = column[String]("LE_IDENT")
        def * = (id, airport_id, airport_ident, surface, le_ident)
    }

    val runways = TableQuery[Runways]

    val init = Await.result(db.run((countries.schema ++ airports.schema ++ runways.schema).create), Duration.Inf)

    def populate(raw_airports: Array[Array[String]],
                 raw_countries: Array[Array[String]],
                 raw_runways: Array[Array[String]]) =
    {
        val insert_countries = countries ++= raw_countries.par.map(arr => (arr(0).toInt, arr(1), arr(2), arr(3))).to(Seq)
        val insert_aitports: DBIO[Option[Int]] = airports ++= raw_airports.par.map(arr =>
            (arr(0).toInt, arr(1), arr(3), arr(7), arr(8), arr(9), arr(10))).to(Seq)
        val insert_runways = runways ++= raw_runways.par.map(arr => (arr(0).toInt, arr(1).toInt, arr(2), arr(5), arr(8))).to(Seq)
        Await.result(db.run((insert_countries >> insert_aitports >> insert_runways)), Duration.Inf)
    }

    def print() = {
        println("airports:")
        Await.result(db.run(airports.take(10).result).map(_.foreach(e => println(e))), Duration.Inf)
        println("countries:")
        Await.result(db.run(countries.take(10).result).map(_.foreach(e => println(e))), Duration.Inf)
        println("runways:")
        Await.result(db.run(runways.take(10).result).map(_.foreach(e => println(e))), Duration.Inf)
    }

    def close(): Unit = db.close()
}
