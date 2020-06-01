import MyDB.{countries, airports, runways, db}

import slick.jdbc.H2Profile.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object DBQueries {
  def report() =
  {
      println("Report called , WIP")
  }

  def nb_airports_per_country() =
  {
    airports.groupBy(c => c.country_ident)
        .map{ case (country_ident, group) => (country_ident, group.map(_.country_ident).length) }
        .sortBy{ case (country_ident, nb_airports) => (nb_airports.desc, country_ident) }
  }

  def most_common_runways() =
  {
    runways.groupBy(r => r.le_ident)
      .map{ case (le_id, group) => (le_id, group.map(_.le_ident).length) }
      .sortBy{ case (le_id, nb_s) => (nb_s.desc, le_id)}
      .take(10)
  }

  def country_runway_types() =
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
  def query(name: String) =
  {
    val q = runways.map(r => (r.airport_id, r.surface))
      .join(airports)
      .on(_._1 === _.id)
      .join(countries)
      .on(_._2.country_ident === _.code)
      .filter(c => (c._2.name.startsWith(name) || c._2.code.startsWith(name)))
      .map{case (r) => (r._2.name, r._1._2.ident, r._1._1._2)}
      .filter(_._3 =!= "")
      .result


    Await.result(db.run(q).map(_.foreach(e => println(e))), Duration.Inf)
  }
}
