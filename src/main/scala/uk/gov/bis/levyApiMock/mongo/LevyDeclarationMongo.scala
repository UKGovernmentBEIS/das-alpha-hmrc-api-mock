package uk.gov.bis.levyApiMock.mongo

import javax.inject._

import play.modules.reactivemongo._
import uk.gov.bis.levyApiMock.data.{LevyDeclarationOps, LevyDeclarationResponse}
import uk.gov.bis.mongo.MongoCollection

import scala.concurrent.ExecutionContext

class LevyDeclarationMongo @Inject()(val mongodb: ReactiveMongoApi) extends MongoCollection[LevyDeclarationResponse] with LevyDeclarationOps {

  override val collectionName = "declarations"

  override def byEmpref(empref: String)(implicit ec: ExecutionContext) = findOne("empref" -> empref)
}
