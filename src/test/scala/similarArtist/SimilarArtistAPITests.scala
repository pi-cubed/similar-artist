package similar_artist

import org.scalatra.test.scalatest._

class SimilarArtistAPITests extends ScalatraFunSuite {

  addServlet(classOf[SimilarArtistAPI], "/*")

  test("GET / on SimilarArtistAPI should return status 200") {
    get("/") {
      status should equal (200)
    }
  }

}
