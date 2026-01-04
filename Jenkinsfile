pipeline {
    agent any

    environment {
        // Define variables
        DOCKER_IMAGE = "chaudhary2511/user-service"
        REGISTRY_CRED = "docker-hub-creds"
    }

    stages {
        // Stage 1: Get the code
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/chaudhary-pc/CreditScoreAnalyzer.git'
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

    }
    post {
        success {
            echo 'Build and Push Successful! Images are now in Docker Hub.'
        }
        failure {
            echo 'Something went wrong.'
        }
    }
}
