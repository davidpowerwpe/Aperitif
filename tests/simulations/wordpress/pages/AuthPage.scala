package wordpress

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.jdbc.Predef._

object AuthPage {
	var header_auth = Map("Upgrade-Insecure-Requests" -> "1")
	// Login
	val login = group("Login")
		{
			exec(
				http("Try User Login")
					.post("/wp-login.php")
					.headers(header_auth)
					.formParam("log", "gatling1")
					.formParam("pwd", "gatling1")
					.formParam("wp-submit", "Log In")
					.formParam("redirect_to", "http://localhost:8080/wp-admin/")
					.formParam("testcookie", "1")
			).pause(1 second, 3 seconds)
		}
	
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