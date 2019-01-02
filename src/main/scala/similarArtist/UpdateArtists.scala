import org.neo4j.driver.v1._

object UpdateArtists {

	val DB_URL = sys.env("GRAPHENEDB_BOLT_URL")
	val DB_USER = sys.env("GRAPHENEDB_BOLT_USER")
	val DB_PASSWORD = sys.env("GRAPHENEDB_BOLT_PASSWORD")
	val driver = GraphDatabase.driver(DB_URL, AuthTokens.basic(DB_USER, DB_PASSWORD))
	val session = driver.session
	
	def main(args: Array[String]) {
		println("updating artists!")
		val script = "MATCH (x) return x;"
		val result = session.run(script).list
		println(result)

		session.close()
		driver.close()
	}
}