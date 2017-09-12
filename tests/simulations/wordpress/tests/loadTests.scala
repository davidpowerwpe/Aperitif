package wordpress

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.jdbc.Predef._

class LoadTestForPostRead extends Simulation {

	val httpConfig = http
		.baseURL("http://localhost:8080")
		.inferHtmlResources(BlackList(""".*\.css""", """.*\.js""", """.*\.ico"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-GB,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:55.0) Gecko/20100101 Firefox/55.0")

	val ViewPostsScenario = scenario("Review 10 Post Pages").exec(AuthPage.login, PostsPage.view, PostsPage.view_first_ten_pages)

	setUp(
		ViewPostsScenario.inject(rampUsers(70) over (1 minutes)),
	).protocols(httpConfig).assertions(global.responseTime.max.lt(1300))
}

class SearchScenario extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:8080")
		.inferHtmlResources(BlackList(""".*\css""", """.*\.js""", """.*\.ico"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:55.0) Gecko/20100101 Firefox/55.0")

	val RunSearchScenario= scenario("Run Search").exec(AuthPage.login, SearchPage.search_for_term)

	setUp(
		RunSearchScenario.inject(rampUsers(100) over (60 seconds)),
	).protocols(httpProtocol).assertions(global.responseTime.max.lt(1200))
}