package wordpress

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.jdbc.Predef._

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

