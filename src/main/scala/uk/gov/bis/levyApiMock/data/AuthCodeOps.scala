package uk.gov.bis.levyApiMock.data

import scala.concurrent.{ExecutionContext, Future}

case class AuthCodeRow(authorizationCode: String, gatewayId: String, redirectUri: String, createdAt: MongoDate, scope: Option[String], clientId: Option[String], expiresIn: Int)

trait AuthCodeOps {
  def find(code: String)(implicit ec: ExecutionContext): Future[Option[AuthCodeRow]]

  def delete(code: String)(implicit ec: ExecutionContext): Future[Int]

  def create(code: String, gatewayUserId: String, redirectUri: String, clientId: String, scope: String)(implicit ec: ExecutionContext): Future[Int]
}
