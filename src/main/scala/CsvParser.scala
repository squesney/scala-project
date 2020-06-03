import scala.io.Source
import scala.collection.parallel.immutable._

object CsvParser {
    def parse(filenames: Array[String]) = {
        val airplanes_source = Source.fromFile(filenames(0))
        val airplanes_info = airplanes_source.getLines().drop(1).toSeq.par
                                             .map(line => line.split(',').padTo(18, "").map(_.replace("\"", ""))).toArray
        val country_source = Source.fromFile(filenames(1))
        val country_info = country_source.getLines().drop(1).toSeq.par
            .map(line => line.split(',').padTo(6, "").map(_.replace("\"", ""))).toArray
        val runway_source = Source.fromFile(filenames(2))
        val runway_info = runway_source.getLines().drop(1).toSeq.par
                                         .map(line => line.split(',').padTo(20, "").map(_.replace("\"", ""))).toArray
        (airplanes_info, country_info, runway_info)
    }
}
