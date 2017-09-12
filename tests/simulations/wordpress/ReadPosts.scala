package wordpress

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

object AuthPage {
	var header_auth = Map("Upgrade-Insecure-Requests" -> "1")
	// Login
	val login = exec(
		http("Login")
			.post("/wp-login.php")
			.headers(header_auth)
			.formParam("log", "gatling1")
			.formParam("pwd", "gatling1")
			.formParam("wp-submit", "Log In")
			.formParam("redirect_to", "http://localhost:8080/wp-admin/")
			.formParam("testcookie", "1")
	).pause(1 second, 3 seconds)

    // Find the logout URL as it contains a validation token required for logout
	var get_logout_url = exec(
		http("Get Logout URL")
			.get("/")
			.check(status.is(200))
			.check(css("#wp-admin-bar-logout a", "href").saveAs("logoutUrl"))
	).pause(1 second)

	// Logout
	var logout = exec(
		http("Logout")
			.get("${logoutUrl}")
			.headers(header_auth)
	)
}

object PostsPage {
	val headers_search = Map(
		"Accept" -> "application/json, text/javascript, */*; q=0.01",
		"Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8",
		"X-Requested-With" -> "XMLHttpRequest")

	// ListPosts
	val view = exec(
		http("Navigate To Posts")
			.get("/wp-admin/edit.php")
			.headers(Map("Upgrade-Insecure-Requests" -> "1"))
	).pause(3 seconds, 5 seconds)
	// Itterate over 
	var view_first_ten_pages = exec(
		repeat(10, "page") {
			// println("Page: ${page}")
			exec(http("Post Page ${page}")
				.get("/wp-admin/edit.php?paged=${page}")
				.check(currentLocation.is("http://localhost:8080/wp-admin/edit.php?paged=${page}"))
		).pause(3 seconds, 5 seconds)
		}
	)
}

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
		ViewPostsScenario.inject(rampUsers(75) over (1 minutes)),
	).protocols(httpConfig)
}