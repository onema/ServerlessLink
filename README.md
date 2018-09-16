# Serverless Link
[![Build Status](https://codebuild.us-east-1.amazonaws.com/badges?uuid=eyJlbmNyeXB0ZWREYXRhIjoiSnM1S0JxcmlUSURBSEpXZEMrakdFdDJHT1JMenBBM2M4UDJhTFV4TC93OFZXaDZzSHJUM1ZQS09acUt5RkM2SzYwQkRqWGpoZmtpTjhyV09XSDR5K0RJPSIsIml2UGFyYW1ldGVyU3BlYyI6IkhYR2s5blQyOXQweFlCU0QiLCJtYXRlcmlhbFNldFNlcmlhbCI6MX0%3D&branch=master)](https://console.aws.amazon.com/codebuild/home?region=us-east-1#/projects/ServerlessLink/view)
[![LICENSE](https://img.shields.io/badge/license-Apache--2.0-blue.svg?longCache=true&style=flat-square)](LICENSE)

Serverless link is a Serverless URL Shortener build on [µServerless](https://github.com/onema/uServerless) and [Scala](https://www.scala-lang.org/)
and deplyed with the serverless framework.

## Demo
**Checkout the [ServerlessLink](http://serverless.link) website for a demo!**

## Setup
Install the AWS CLI, the serverless framework:

```bash
npm install -g serverless
pip install --upgrade awscli
```

Create the assembly

```bash
sbt clean assembly
```

Deploy the code using serverless

```bash
serverless deploy --domain <DOMAIN_NAME>
```
The <DOMAIN_NAME> is the domain you will be using for your application. This will create a bucket with the given domain name. 
You can create an alias `A` record in route 53 and point it to the 


To push the code to the bucket use the following command

```bash
aws s3 cp ./public/ s3://<DOMAIN_NAME> --recursive
```

## Application configuration
The application needs a `config.js` file. You can copy the `public/js/dist.config.js` to `public/js/config.js`. At this time
all you need to fill in is the name of the `invokeUrl` which is the URL of your API, and the scheme, either `https` or `http`.

> **NOTE**: 
>
> I Recommend creating a custom domain for your API gateway e.g. `a.custom.link`. 
This link will be used by default to generate the short URL. 

## Route 53 configuration 
Once you have your API gateway and S3 buckets in place, you should create domains names in route 53 for your static website 
(must match the domain name you gave to the serverless application) and  the api gateway domain.

## Creating a Build/Deploy service for your application
I've included a `code-build-cicd_cfn.yml` template in the infrastructure directory. This template creates a CodeBuild service
linked to your GitHub repo and deploys it for you! 