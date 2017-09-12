package gatlingTest

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import io.gatling.jdbc.Predef._

object Search{

    val feeder = csv("search.csv").random // 1, 2

 	val headers_1 = Map(
		"Accept" -> "*/*",
		"Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8",
		"X-Requested-With" -> "XMLHttpRequest")

	val navigate =  exec(http("navigate")
			.get("/wp-admin/plugin-install.php")
			.headers(Map("Upgrade-Insecure-Requests" -> "1")))
		.pause(15)

	val search_for_term = exec(http("Home")
   			.get("/"))
   			.pause(1)
    		.feed(feeder) // 3	
	        .exec(http("run_search")
			.post("/wp-admin/admin-ajax.php")
			.headers(headers_1)
			.formParam("_ajax_nonce", "6906464983")
			.formParam("s", "Dolly")
			.formParam("tab", "search")
			.formParam("type", "term")
			.formParam("pagenow", "plugin-install")
			.formParam("action", "search-install-plugins")
			.check(bodyBytes.is(RawFileBody("SearchScenario_0001_response.txt"))))
		.pause(6)
}

class SearchScenario extends Simulation {

	val httpProtocol = http
		.baseURL("http://localhost:8080")
		.inferHtmlResources(BlackList(""".*\css""", """.*\.js""", """.*\.ico"""), WhiteList())
		.acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
		.acceptEncodingHeader("gzip, deflate")
		.acceptLanguageHeader("en-US,en;q=0.5")
		.userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:55.0) Gecko/20100101 Firefox/55.0")

	val RunSearchScenario= scenario("Run Search").exec(Auth.login,Search.navigate)

	setUp(
		RunSearchScenario.inject(rampUsers(100) over (60 seconds)),
	).protocols(httpProtocol).assertions(global.responseTime.max.lt(1200))
}

