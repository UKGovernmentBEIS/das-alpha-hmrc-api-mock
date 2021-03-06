package uk.gov.bis.levyApiMock.data

import scala.concurrent.{ExecutionContext, Future}

case class GatewayUser(gatewayID: String, password: String, empref: Option[String], nameLine1: Option[String], nameLine2: Option[String])

trait GatewayUserOps {
  def forGatewayID(gatewayID: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]]

  def forEmpref(empref: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]]

  def validate(gatewayID: String, password: String)(implicit ec: ExecutionContext): Future[Option[GatewayUser]]
}
