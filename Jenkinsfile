pipeline {
    agent any

    environment {
        COMPOSE_PROJECT_NAME = "credit-score"
        AWS_ACCOUNT_ID = credentials('aws-account-id')
        AWS_DEFAULT_REGION = credentials('aws-default-region')
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                git branch: 'master', url: 'https://github.com/chaudhary-pc/CreditScoreAnalyzer.git'
            }
        }

        stage('Login to ECR') {
            steps {
                sh '''
                    aws ecr get-login-password --region ${AWS_DEFAULT_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_DEFAULT_REGION}.amazonaws.com
                '''
            }
        }

        stage('Build, Test & Push Images') {
            failFast true
            parallel {
                stage('User Service') {
                    steps {
                        dir('user-service') {
                            script {
                                def imageTag = env.GIT_COMMIT.substring(0, 7)
                                def repositoryUri = "${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_DEFAULT_REGION}.amazonaws.com"
                                env.USER_SERVICE_IMAGE = "${repositoryUri}/user-service:${imageTag}"
                                sh 'chmod +x mvnw'
                                sh './mvnw clean package -DskipTests'
                                def img = docker.build(env.USER_SERVICE_IMAGE, ".")
                                img.push()
                            }
                        }
                    }
                }
                stage('API Gateway') {
                    steps {
                        dir('api-gateway') {
                            script {
                                def imageTag = env.GIT_COMMIT.substring(0, 7)
                                def repositoryUri = "${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_DEFAULT_REGION}.amazonaws.com"
                                env.API_GATEWAY_IMAGE = "${repositoryUri}/api-gateway:${imageTag}"
                                sh 'chmod +x mvnw'
                                sh './mvnw clean package -DskipTests'
                                def img = docker.build(env.API_GATEWAY_IMAGE, ".")
                                img.push()
                            }
                        }
                    }
                }
                stage('Discovery Server') {
                    steps {
                        dir('discovery-server') {
                            script {
                                def imageTag = env.GIT_COMMIT.substring(0, 7)
                                def repositoryUri = "${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_DEFAULT_REGION}.amazonaws.com"
                                env.DISCOVERY_SERVER_IMAGE = "${repositoryUri}/discovery-server:${imageTag}"
                                sh 'chmod +x mvnw'
                                sh './mvnw clean package -DskipTests'
                                def img = docker.build(env.DISCOVERY_SERVER_IMAGE, ".")
                                img.push()
                            }
                        }
                    }
                }
                stage('Data Collection Service') {
                    steps {
                        dir('data-collection-service') {
                            script {
                                def imageTag = env.GIT_COMMIT.substring(0, 7)
                                def repositoryUri = "${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_DEFAULT_REGION}.amazonaws.com"
                                env.DATA_COLLECTION_SERVICE_IMAGE = "${repositoryUri}/data-collection-service:${imageTag}"
                                sh 'chmod +x mvnw'
                                sh './mvnw clean package -DskipTests'
                                def img = docker.build(env.DATA_COLLECTION_SERVICE_IMAGE, ".")
                                img.push()
                            }
                        }
                    }
                }
                stage('Credit Scoring Service') {
                    steps {
                        dir('credit-scoring-service') {
                            script {
                                def imageTag = env.GIT_COMMIT.substring(0, 7)
                                def repositoryUri = "${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_DEFAULT_REGION}.amazonaws.com"
                                env.CREDIT_SCORING_SERVICE_IMAGE = "${repositoryUri}/credit-scoring-service:${imageTag}"
                                sh 'chmod +x mvnw'
                                sh './mvnw clean package -DskipTests'
                                def img = docker.build(env.CREDIT_SCORING_SERVICE_IMAGE, ".")
                                img.push()
                            }
                        }
                    }
                }
                stage('Report Service') {
                    steps {
                        dir('report-service') {
                            script {
                                def imageTag = env.GIT_COMMIT.substring(0, 7)
                                def repositoryUri = "${env.AWS_ACCOUNT_ID}.dkr.ecr.${env.AWS_DEFAULT_REGION}.amazonaws.com"
                                env.REPORT_SERVICE_IMAGE = "${repositoryUri}/report-service:${imageTag}"
                                sh 'chmod +x mvnw'
                                sh './mvnw clean package -DskipTests'
                                def img = docker.build(env.REPORT_SERVICE_IMAGE, ".")
                                img.push()
                            }
                        }
                    }
                }
            }
        }

        stage('Deploy Services') {
            steps {
                sh """
                    docker-compose -f docker-compose.prod.yml up -d --remove-orphans
                    docker image prune -f
                """
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}