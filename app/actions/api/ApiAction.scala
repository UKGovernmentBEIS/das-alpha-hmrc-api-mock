package actions.api

import javax.inject.Singleton

import com.google.inject.Inject
import db.levy.GatewayIdSchemeDAO
import db.outh2.AccessTokenDAO
import play.api.Logger
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class ApiRequest[A](val request: Request[A], val emprefs: List[String]) extends WrappedRequest[A](request)

@Singleton
class ApiAction @Inject()(accessTokens: AccessTokenDAO, enrolments: GatewayIdSchemeDAO)(implicit ec: ExecutionContext)
  extends ActionBuilder[ApiRequest]
    with ActionRefiner[Request, ApiRequest] {

  override protected def refine[A](request: Request[A]): Future[Either[Result, ApiRequest[A]]] = {
    implicit val rh: RequestHeader = request

    val BearerToken = "Bearer (.+)".r
    request.headers.get("Authorization") match {
      case Some(BearerToken(accessToken)) => accessTokens.find(accessToken).flatMap {
        case Some(at) =>
          enrolments.emprefsForId(at.gatewayId).map { emprefs =>
            Right(new ApiRequest(request, emprefs.toList))
          }
        case _ =>
          Logger.info(s"No authorization found for Bearer $accessToken")
          Future.successful(Left(Unauthorized(s"No authorization found for Bearer $accessToken")))
      }
      case _ =>
        Logger.info("No Authorization header found")
        Future.successful(Left(Unauthorized("No Authorization header found")))
    }
  }
}
