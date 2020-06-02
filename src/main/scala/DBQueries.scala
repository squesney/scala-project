import MyDB.{airports, countries, db, runways}
import slick.jdbc.H2Profile.api._

import scala.<:<
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Success
import scala.collection.parallel.immutable._
import scala.collection.parallel.CollectionConverters._

object DBQueries {
    def report() =
    {
        val report1 = db.run(nb_airports_per_country.result)
        val report2 = db.run(most_common_runways.result)
        val report3 = db.run(country_runway_types.result)
        val res1 = Await.result(report1, Duration.Inf)
        println("Top 10 countries with the most airports:")
        res1.take(10).foreach(println(_))
        println("Top 10 countries with the less airports:")
        res1.takeRight(10).foreach(println(_))
        val res2 = Await.result(report2, Duration.Inf)
        println("Top 10 most common runway latitude: ")
        res2.foreach(println(_))
        val res3 = Await.result(report3, Duration.Inf)
        val map = res3.groupMap(_._1)(_._3).view.mapValues(_.distinct).toMap
        map.foreach{ case (country, runways) => println(s"Runway types in $country:"); runways.foreach(println)}
    }

    def nb_airports_per_country: Query[(Rep[String], Rep[Int]), (String, Int), Seq] =
    {
        airports
            .join(countries)
            .on(_.country_ident === _.code)
            .groupBy(c => c._1.country_ident)
            .map{ case (c, group) => (c, group.map(_._1.country_ident).length) }
            .sortBy{ case (country_ident, nb_airports) => (nb_airports.desc, country_ident) }
    }

    def most_common_runways: Query[(Rep[String], Rep[Int]), (String, Int), Seq] =
    {
        runways.groupBy(r => r.le_ident)
            .map{ case (le_id, group) => (le_id, group.map(_.le_ident).length) }
            .sortBy{ case (le_id, nb_s) => (nb_s.desc, le_id)}
            .take(10)
    }

    def country_runway_types: Query[(Rep[String], Rep[String], Rep[String]), (String, String, String), Seq] =
    {
        runways.map(r => (r.airport_id, r.surface))
            .join(airports)
            .on(_._1 === _.id)
            .join(countries)
            .on(_._2.country_ident === _.code)
            .map{case (r) => (r._2.name, r._1._2.ident, r._1._1._2)}
            .filter(_._3 =!= "")
    }

    // Query Option will ask the user for the country name or code
    // and display the airports & runways at each airport. The input can be country code or country name.
    def query(name: String): Unit =
    {
        val q = runways.map(r => (r.airport_id, r.surface, r.le_ident))
            .join(airports)
            .on(_._1 === _.id)
            .join(countries)
            .on(_._2.country_ident === _.code)
            .filter(c => (c._2.name.toLowerCase.startsWith(name) || c._2.code.toLowerCase.startsWith(name)))
            .map{r => (r._2.name, r._1._2.name, r._1._1._2, r._1._1._3)}
            .sortBy(r => (r._1, r._2, r._3, r._4))
            .result


        val res = Await.result(db.run(q), Duration.Inf)
        val map = res.groupMap(_._1)(e => (e._2, e._3, e._4)).view.mapValues(_.groupMap(_._1)(e => (e._2, e._3))).toMap
        map.foreach{ case (country, value) => println(s"country: $country");
            value.foreach{ case (airport, runways) => println(s"Airport: $airport\nRunways:"); runways.foreach(println(_))}}
    }
}
