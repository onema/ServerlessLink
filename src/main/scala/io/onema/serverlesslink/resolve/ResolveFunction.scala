package io.onema.serverlesslink.resolve

import com.amazonaws.serverless.proxy.model.{AwsProxyRequest, AwsProxyResponse}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import com.amazonaws.services.lambda.runtime.Context
import io.onema.userverless.configuration.cors.{CorsConfiguration, EnvCorsConfiguration}
import io.onema.userverless.configuration.lambda.EnvLambdaConfiguration
import io.onema.userverless.function.ApiGatewayHandler
import org.apache.http.HttpStatus

import scala.collection.JavaConverters._


class ResolveFunction extends ApiGatewayHandler with EnvLambdaConfiguration {

  //--- Fields ---
  private val dynamo = AmazonDynamoDBAsyncClientBuilder.defaultClient()
  private val tableName = getValue("TABLE_NAME").getOrElse("")
  val logic = new ResolveLogic(dynamo, tableName)

  //--- Methods ---
  def corsConfiguration(origin: Option[String]): CorsConfiguration = EnvCorsConfiguration(origin)

  def execute(request: AwsProxyRequest, context: Context): AwsProxyResponse = {
    val map = request.getPathParameters.asScala
    val resolvedLink = logic.process(map("id").toLowerCase)
    buildResponse(HttpStatus.SC_TEMPORARY_REDIRECT, Map("Location" -> resolvedLink))
  }
}
