package auth

import java.security.SecureRandom
import java.sql.Date
import javax.inject.Inject

import cats.data.OptionT
import cats.std.future._
import db.client.{DASUserDAO, DASUserRow}
import db.outh2._
import org.apache.commons.codec.binary.Hex
import org.mindrot.jbcrypt.BCrypt

import scala.concurrent.{ExecutionContext, Future}
import scalaoauth2.provider._


class DASDataHandler @Inject()(implicit ec: ExecutionContext, dasUsers: DASUserDAO, clients: ClientDAO, accessTokens: AccessTokenDAO, authCodeDAO: AuthCodeDAO) extends DataHandler[DASUserRow] {
  override def validateClient(request: AuthorizationRequest): Future[Boolean] = {
    request.clientCredential match {
      case Some(cred) => clients.validate(cred.clientId, cred.clientSecret, request.grantType)
      case None => Future.successful(false)
    }
  }

  private val random = new SecureRandom()
  random.nextBytes(new Array[Byte](55))

  def generateToken: String = {
    val bytes = new Array[Byte](12)
    random.nextBytes(bytes)
    new String(Hex.encodeHex(bytes))
  }

  override def createAccessToken(authInfo: AuthInfo[DASUserRow]): Future[AccessToken] = {
    val accessTokenExpiresIn = Some(60L * 60L) // 1 hour
    val refreshToken = Some(generateToken)
    val accessToken = generateToken
    val createdAt = new Date(System.currentTimeMillis())
    val tokenObject = AccessTokenRow(accessToken, refreshToken, authInfo.user.id, authInfo.scope, accessTokenExpiresIn, createdAt, authInfo.clientId)
    accessTokens.deleteExistingAndCreate(tokenObject).map { _ =>
      AccessToken(accessToken, refreshToken, authInfo.scope, accessTokenExpiresIn, createdAt)
    }
  }

  override def refreshAccessToken(authInfo: AuthInfo[DASUserRow], refreshToken: String): Future[AccessToken] = ???

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[DASUserRow]]] = ???

  override def getStoredAccessToken(authInfo: AuthInfo[DASUserRow]): Future[Option[AccessToken]] = {
    OptionT(accessTokens.find(authInfo.user.id, authInfo.clientId)).map { token =>
      AccessToken(token.accessToken, token.refreshToken, token.scope, token.expiresIn, token.createdAt)
    }.value
  }

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[DASUserRow]]] = {


    val ot = for {
      token <- OptionT(authCodeDAO.find(code))
      user <- OptionT(dasUsers.byId(token.userId))
    } yield AuthInfo(user, token.clientId, token.scope, token.redirectUri)

    ot.value
  }

  override def deleteAuthCode(code: String): Future[Unit] = authCodeDAO.delete(code).map(_ => ())

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[DASUserRow]]] = ???

  override def findAccessToken(token: String): Future[Option[AccessToken]] = ???

  override def findUser(request: AuthorizationRequest): Future[Option[DASUserRow]] = {
    request.clientCredential.map { cred =>
      dasUsers.byName(cred.clientId).map(_.filter(u => BCrypt.checkpw(cred.clientSecret.get, u.hashedPassword)))
    } match {
      case None => Future.successful(None)
      case Some(f) => f
    }
  }
}
