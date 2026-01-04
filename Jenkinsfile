pipeline {
    agent any

    environment {
        // Define variables
        DOCKER_IMAGE = "your-dockerhub-username/user-service"
        REGISTRY_CRED = "docker-hub-creds"
        AWS_CRED = "ec2-ssh-key"
        EC2_IP = "1.2.3.4" // REPLACE WITH YOUR AWS IP
    }

    stages {
        // Stage 1: Get the code
        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/YOUR_USERNAME/CreditScoreAnalyzer.git'
            }
        }

        // Stage 2: Compile Java
        stage('Build JAR') {
            steps {
                // We use the Maven Wrapper included in your project
                // 'sh' is for Linux/Mac, 'bat' is for Windows.
                // Since Jenkins is running in Linux container, use 'sh'
                dir('user-service') {
                    sh 'chmod +x mvnw'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        // Stage 3: Build Docker Image
        stage('Build Image') {
            steps {
                dir('user-service') {
                    script {
                        // Uses the Docker plugin to build
                        docker.build("${DOCKER_IMAGE}:latest")
                    }
                }
            }
        }

        // Stage 4: Push to Docker Hub
        stage('Push Image') {
            steps {
                script {
                    // Logs in, pushes, and logs out automatically
                    docker.withRegistry('', REGISTRY_CRED) {
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        // Stage 5: Deploy to AWS
        stage('Deploy to EC2') {
            steps {
                sshagent([AWS_CRED]) {
                    // We SSH into the server and run commands
                    sh """
                        ssh -o StrictHostKeyChecking=no ubuntu@${EC2_IP} '
                            docker pull ${DOCKER_IMAGE}:latest
                            docker stop user-service || true
                            docker rm user-service || true
                            docker run -d --name user-service -p 8081:8081 ${DOCKER_IMAGE}:latest
                        '
                    """
                }
            }
        }
    }
}