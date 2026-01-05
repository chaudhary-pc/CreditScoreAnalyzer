pipeline {
    agent any

    environment {
        DOCKER_HUB_USER = "your-dockerhub-username"
        REGISTRY_CRED = "docker-hub-creds"
        AWS_CRED = "ec2-ssh-key"
        EC2_IP = "1.2.3.4"
        // We need to pass the username to the docker-compose file on the server
        COMPOSE_PROJECT_NAME = "credit-score"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/YOUR_USERNAME/CreditScoreAnalyzer.git'
            }
        }

		// --- SERVICE 1: USER SERVICE ---
        stage('Build User Service') {
            steps {
                dir('user-service') {
                    // 1. Compile Java
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'

                    // 2. Build & Push Docker Image
                    script {
                        docker.withRegistry('', REGISTRY_CRED) {
                            def img = docker.build("${DOCKER_HUB_USER}/user-service:latest")
                            img.push()
                        }
                    }
                }
            }
        }

        // --- SERVICE 2: API GATEWAY ---
        stage('Build API Gateway') {
            steps {
                dir('api-gateway') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                    script {
                        docker.withRegistry('', REGISTRY_CRED) {
                            def img = docker.build("${DOCKER_HUB_USER}/api-gateway:latest")
                            img.push()
                        }
                    }
                }
            }
        }

		// --- SERVICE 3: DISCOVERY SERVER ---
        stage('Build Discovery Server') {
            steps {
                dir('discovery-server') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                    script {
                        docker.withRegistry('', REGISTRY_CRED) {
                            def img = docker.build("${DOCKER_HUB_USER}/discovery-server:latest")
                            img.push()
                        }
                    }
                }
            }
        }

        // --- SERVICE 4: DATA COLLECTION ---
        stage('Build Data Service') {
            steps {
                dir('data-collection-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                    script {
                        docker.withRegistry('', REGISTRY_CRED) {
                            def img = docker.build("${DOCKER_HUB_USER}/data-collection-service:latest")
                            img.push()
                        }
                    }
                }
            }
        }

		// --- SERVICE 5: CREDIT SCORING ---
        stage('Build Credit Service') {
            steps {
                dir('credit-scoring-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                    script {
                        docker.withRegistry('', REGISTRY_CRED) {
                            def img = docker.build("${DOCKER_HUB_USER}/credit-scoring-service:latest")
                            img.push()
                        }
                    }
                }
            }
        }

        // --- SERVICE 6: REPORT SERVICE ---
        stage('Build Report Service') {
            steps {
                dir('report-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                    script {
                        docker.withRegistry('', REGISTRY_CRED) {
                            def img = docker.build("${DOCKER_HUB_USER}/report-service:latest")
                            img.push()
                        }
                    }
                }
            }
        }

        // --- DEPLOY STAGE ---
        stage('Deploy to AWS') {
            steps {
                sshagent([AWS_CRED]) {
                    // We use 'scp' (Secure Copy) to send the file from Jenkins to AWS
                    sh "scp -o StrictHostKeyChecking=no docker-compose.prod.yml ubuntu@${EC2_IP}:/home/ubuntu/docker-compose.yml"
					sh "scp -o StrictHostKeyChecking=no config-repo/promtail-config.yaml ubuntu@${EC2_IP}:/home/ubuntu/promtail-config.yaml"

					// 2. SSH in and run Docker Compose
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                            # Export the variable so docker-compose can see it
                            export DOCKER_USERNAME=${DOCKER_HUB_USER}

                            # Pull the latest images for all services
                            docker-compose pull

                            docker-compose up -d --remove-orphans

                            # Clean up old images to save disk space
                            docker image prune -f
                        '
                    """
                }
            }
        }
    }
}