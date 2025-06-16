pipeline {

  environment {
    dockerimagename = "rohannagcalsoft/guess-number-app"
    dockerImage = ""
  }

  agent any

  stages {

    stage('Install Maven') {
      steps {
        sh '''
          if ! command -v mvn &> /dev/null; then
            echo "Maven not found, installing..."
            sudo apt-get update
            sudo apt-get install -y maven
          else
            echo "Maven already installed."
          fi
        '''
      }
    }

    stage('Maven Build') {
      steps {
        sh 'mvn clean package'
      }
    }

    stage('Build image') {
      steps{
        script {
          dockerImage = docker.build(dockerimagename)
        }
      }
    }

    stage('Pushing Image') {
      environment {
        registryCredential = 'dockerhub-credentials'
      }
      steps{
        script {
          docker.withRegistry('https://registry.hub.docker.com', registryCredential) {
            dockerImage.push("latest")
          }
        }
      }
    }

    stage('Deploying App to Kubernetes') {
      steps {
        script {
          kubernetesDeploy(configs: "deployment.yaml,service.yaml", kubeconfigId: "gke_deployer-credentials")
        }
      }
    }
  }

}