package wordpress

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.jdbc.Predef._

object Config {
	val httpConfig = http
		.baseURL("http://localhost:8080")
		.inferHtmlResources(BlackList(""".*\.css""", """.*\.js""", """.*\.ico"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:55.0) Gecko/20100101 Firefox/55.0")
}

class VolumeTestPostRead extends Simulation {
	val ViewPostsScenario = scenario("Load: Review 10 Post Pages Per User").exec(AuthPage.login, PostsPage.view, PostsPage.view_first_ten_pages)

	setUp(
		ViewPostsScenario.inject(rampUsers(50) over (1 minutes))).protocols(Config.httpConfig)
		    .assertions(details("Login"/"Try User Login").responseTime.max.lt(300))
			.assertions(details("Page").responseTime.max.lt(300))
			.assertions(forAll.responseTime.max.lt(800))
			.assertions(global.failedRequests.percent.is(0))
}

class StressTestPostRead extends Simulation {
	val ViewPostsScenario = scenario("Stress: Review 10 Post Pages Per User").exec(AuthPage.login, PostsPage.view, PostsPage.stress_view_first_ten_pages)

	setUp(
		ViewPostsScenario.inject(rampUsers(500) over (10 minutes))).protocols(Config.httpConfig)
		    .assertions(details("Login"/"Try User Login").responseTime.max.lt(300))
			.assertions(details("Page"/"Change Page").responseTime.max.lt(300))
			.assertions(forAll.responseTime.max.lt(800))
			.assertions(global.failedRequests.percent.is(0))
}