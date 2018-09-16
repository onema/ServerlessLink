package io.onema.serverlesslink.create

import com.amazonaws.serverless.proxy.model.{AwsProxyRequest, AwsProxyResponse}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import com.amazonaws.services.lambda.runtime.Context
import io.onema.userverless.configuration.cors.{CorsConfiguration, EnvCorsConfiguration}
import io.onema.userverless.configuration.lambda.EnvLambdaConfiguration
import io.onema.userverless.function.ApiGatewayHandler
import io.onema.json.Extensions._
import io.onema.serverlesslink.create.CreateFunction.{LinkMapping, ResponseBody}
import org.apache.http.HttpStatus


class CreateFunction extends ApiGatewayHandler with EnvLambdaConfiguration {

  //--- Fields ---
  private val dynamo = AmazonDynamoDBAsyncClientBuilder.defaultClient()
  private val tableName = getValue("TABLE_NAME").getOrElse("")
  val logic = new CreateLogic(dynamo, tableName)

  //--- Methods ---
  override def corsConfiguration(origin: Option[String]): CorsConfiguration = EnvCorsConfiguration(origin)

  def execute(request: AwsProxyRequest, context: Context): AwsProxyResponse = {
    val host = request.getHeaders.get("Host")

    cors(request) {
      val map = request.getBody.jsonDecode[LinkMapping]
      val linkId = logic.process(map.url)
      buildResponse(HttpStatus.SC_ACCEPTED, ResponseBody(linkId, s"$host/$linkId"))
    }
  }
}

object CreateFunction {
  case class LinkMapping(url: String)
  case class ResponseBody(id: String, url: String)
}
