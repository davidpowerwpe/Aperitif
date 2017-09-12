package wordpress

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.jdbc.Predef._

object SearchPage {


    //read in from search.csv
    val feeder = csv("search.csv").random

	//Search for term
	val search_for_term = exec(http("Home")
   			.get("/"))
   			.pause(1)
    		.feed(feeder) // 3	
	        .exec(http("run_search_${searchCriterion}")
			.post("/wp-admin/admin-ajax.php")
			.headers(Map(
				"Accept" -> "*/*",
				"Content-Type" -> "application/x-www-form-urlencoded; charset=UTF-8",
				"X-Requested-With" -> "XMLHttpRequest"))
			.formParam("_ajax_nonce", "217e3d9c02")
			.formParam("s", "${searchCriterion}")
			.formParam("tab", "search")
			.formParam("type", "term")
			.formParam("pagenow", "plugin-install")
			.formParam("action", "search-install-plugins")
			.check(bodyBytes.is(RawFileBody("SearchScenario_0001_response.txt"))))
	    	.pause(6)
}