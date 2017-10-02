package uk.gov.bis.levyApiMock.data.stubs

import uk.gov.bis.oauth.data.{AuthRecord, AuthRecordOps}

import scala.concurrent.{ExecutionContext, Future}

trait StubAuthRecordOps extends AuthRecordOps {
  override def forRefreshToken(refreshToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = ???

  override def forAccessToken(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = ???

  override def find(accessToken: String)(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = ???

  override def find(gatewayId: String, clientId: Option[String])(implicit ec: ExecutionContext): Future[Option[AuthRecord]] = ???

  override def create(record: AuthRecord)(implicit ec: ExecutionContext): Future[Unit] = ???

  override def deleteExistingAndCreate(existing: AuthRecord, created: AuthRecord)(implicit ec: ExecutionContext): Future[Unit] = ???
}

object StubAuthRecordOps extends StubAuthRecordOps