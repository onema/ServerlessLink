package io.onema.serverlesslink.create

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync
import com.amazonaws.services.dynamodbv2.model.{AttributeValue, PutItemResult}
import com.typesafe.scalalogging.Logger
import scala.collection.JavaConverters._


class CreateLogic(val dynamodbClient: AmazonDynamoDBAsync, val tableName: String) {

  //--- Fields ---
  protected val log = Logger("logic")

  //--- Methods ---
  def process(value: String): String = {
    val hash = makeLinkId(value)
    log.info(s"HASH: $hash")
    val linkId = hash.take(4)
    recordLink(linkId, value)
    linkId
  }

  def makeLinkId(value: String): String = {
    java.security.MessageDigest.getInstance("SHA-1")
      .digest(value.getBytes).map((b: Byte) => (if (b >= 0 & b < 16) "0" else "") + (b & 0xFF).toHexString)
      .mkString.toLowerCase
  }


  def recordLink(linkId: String, value: String, expires: Boolean = true): PutItemResult = {
    log.debug(s"linkId: $linkId, mapping value: $value, table name: $tableName")
    dynamodbClient.putItem(
      tableName,
      Map(
        "LinkId" -> new AttributeValue().withS(linkId),
        "Value" -> new AttributeValue().withS(value),
      ).asJava
    )
  }
}