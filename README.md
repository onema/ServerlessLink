# Serverless Link
[![Build Status](https://codebuild.us-east-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiSnM1S0JxcmlUSURBSEpXZEMrakdFdDJHT1JMenBBM2M4UDJhTFV4TC93OFZXaDZzSHJUM1ZQS09acUt5RkM2SzYwQkRqWGpoZmtpTjhyV09XSDR5K0RJPSIsIml2UGFyYW1ldGVyU3BlYyI6IkhYR2s5blQyOXQweFlCU0QiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)](https://console.aws.amazon.com/codebuild/home?region=us-east-1#/projects/ServerlessLink/view)
[![LICENSE](https://img.shields.io/badge/license-Apache--2.0-blue.svg?longCache=true&style=flat-square)](LICENSE)

Serverless link is a Serverless URL Shortener build on [µServerless](https://github.com/onema/uServerless) and [Scala](https://www.scala-lang.org/)
and deployed using the [serverless framework](https://serverless.com).

## Demo
**Checkout the [ServerlessLink](http://serverless.link) website for a demo!**

## Requirements
1. python `3.6+` ([download](https://www.python.org/downloads/))
  * This is used to install the AWS CLI and move the website assets into the S3 bucket
1. java JDK `8+` ([download](https://www.java.com/en/download/))
1. sbt `1+` ([install](https://www.scala-sbt.org/1.0/docs/Setup.html))

### Using docker
Alternatively you can use the `onema/userverless-build` docker container to build and deploy your application:
```bash
docker run -it --rm -v $HOME/.aws/:/root/.aws/ -v $PWD:/root/app onema/userverless-build bash
```

## Setup
### Create the assembly

```bash
sbt assembly
```

### Deploy the code using serverless

```bash
serverless deploy --domain <DOMAIN_NAME>
```
The <DOMAIN_NAME> is the domain you will be using for your application. This will create a bucket with the given domain name. 
You can create an alias `A` record in route 53 and point it to the bucket.

Serverless will use the file `infrastructure/link-resources_cfn.yml` and generate the following resources:

* Website Bucket (to place the application files)
* DynamoDB table (where the link mappings are saved)
* ACM certificate for the API 
* Domain Mapping for the API (the domain will be `m.<DOMAIN_NAME>`)

> **IMPORTANT**:
> 
> The process will not finished until the ACM Certificate has been approved, the approval process requires a DNS entry, 
> you can see the instructions to add the DNS record in the [ACM](https://console.aws.amazon.com/acm/home?region=us-east-1#) control panel.  

### To push the code to the bucket use the following command

```bash
aws s3 cp ./public/ s3://<DOMAIN_NAME> --recursive
```

### DNS Setup
Once you have properly setup the application you need to add two DNS records for your applicaiton and the API.
In Route 53, create an `A` or `CNAME` record using an *Alias* for your bucket. This name must match the bucket name which 
is named after the parameter that you passed to the serverless application above `<DOMAIN_NAME>`.

The second is a `CNAME` record to the API Gateway named `m.<DOMAIN_NAME>`.


## Application configuration
The application needs a `config.js` file. You can copy the `public/js/dist.config.js` to `public/js/config.js`. At this time
all you need to fill in is the name of the `invokeUrl` which is the URL of your API `m.<DOMAIN_NAME>`, and the scheme, this should be `https`.

## Route 53 configuration 
Once you have your API gateway and S3 buckets in place, you should create domains names in route 53 for your static website 
(must match the domain name you gave to the serverless application) and  the api gateway domain.

## Creating a 
I've included a `code-build-cicd_cfn.yml` template in the infrastructure directory. This template creates a CodeBuild service
linked to your GitHub repo and deploys it for you! 

## Optional setup Build/Deploy service for your application
I have included a cloud formation template `infrastructure/code-build-cicd_cfn.yml` to generate a CodeBuild service. 
This service can be used to deployed your serverless application  any time you push code to a branch in your GitHub repository 
(the branch is defined in the template parameters).
