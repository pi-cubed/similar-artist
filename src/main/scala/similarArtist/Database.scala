package similar_artist

import org.neo4j.driver.v1._
// import java.time.Duration


class Database {

	val dbConfig = AuthTokens.basic(
		sys.env("GRAPHENEDB_BOLT_USER"), sys.env("GRAPHENEDB_BOLT_PASSWORD")
	)
	val dbDriver = GraphDatabase.driver(sys.env("GRAPHENEDB_BOLT_URL"), dbConfig)


	def tryWith[A <: AutoCloseable, B](closeable: A)(f: (A) => B): B = {
	    try {
	        f(closeable)
	    } finally {
	        closeable.close
	    }
	}


	def withTransaction[A](f: Transaction => A): A = {
		// val config = TransactionConfig.builder
  //                .withTimeout(Duration.ofMinutes(1))
  //                .build

		tryWith(dbDriver.session) { session =>
			tryWith(session.beginTransaction) { tx =>
				val result = f(tx)
				tx.success()
				result
			}
		}
	}


	def close = {
		dbDriver.close
	}

}
