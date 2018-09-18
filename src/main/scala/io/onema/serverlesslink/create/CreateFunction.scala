package io.onema.serverlesslink.create

import com.amazonaws.serverless.proxy.model.{AwsProxyRequest, AwsProxyResponse}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClientBuilder
import com.amazonaws.services.lambda.runtime.Context
import io.onema.json.Extensions._
import io.onema.serverlesslink.create.CreateFunction.{LinkMapping, ResponseBody}
import io.onema.userverless.configuration.cors.{CorsConfiguration, EnvCorsConfiguration}
import io.onema.userverless.configuration.lambda.EnvLambdaConfiguration
import io.onema.userverless.exception.HandleRequestException
import io.onema.userverless.function.ApiGatewayHandler
import org.apache.http.HttpStatus

import scala.util.{Failure, Success, Try}


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
      val map = Try(request.getBody.jsonDecode[LinkMapping]) match {
        case Success(result) => result
        case Failure(_) =>
          throw new HandleRequestException(
            HttpStatus.SC_BAD_REQUEST,
            s"Unable to use payload '${request.getBody}', please use a payload that conforms to '${LinkMapping("value").asJson}'"
          )
      }
      val linkId = logic.process(map.url)
      buildResponse(HttpStatus.SC_ACCEPTED, ResponseBody(linkId, s"$host/$linkId"))
    }
  }
}

object CreateFunction {
  case class LinkMapping(url: String)
  case class ResponseBody(id: String, url: String)
}
