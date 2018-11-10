package io.onema.serverlesslink.resolve

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item}
import com.typesafe.scalalogging.Logger
import io.onema.userverless.exception.HandleRequestException
import org.apache.http.HttpStatus

import scala.util.{Failure, Success, Try}


class ResolveLogic(val dynamodbClient: AmazonDynamoDBAsync, val tableName: String) {

  //--- Fields ---
  protected val log = Logger("logic")
  private val dynamoDb = new DynamoDB(dynamodbClient)
  private val table = dynamoDb.getTable(tableName)

  //--- Methods ---
  def process(linkId: String): String = {
    val value = findMapping(linkId).getJSON("Value")
    log.info(s"Found value: $value")
    value.stripPrefix("\"").stripSuffix("\"").trim
  }

  private def findMapping(linkId: String): Item = {
    val query = new QuerySpec()
      .withKeyConditionExpression("LinkId = :v_linkid")
      .withValueMap(new ValueMap().withString(":v_linkid", linkId))
    Try(table.query(query).iterator().next()) match {
      case Success(item) => item
      case Failure(_: NoSuchElementException) =>
        throw new HandleRequestException(HttpStatus.SC_BAD_REQUEST, s"""Unable to find link with ID "$linkId" """)
      case Failure(ex: Throwable) => throw ex
    }
  }
}
