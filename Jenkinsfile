pipeline {

  environment {
    dockerimagename = "rohannagcalsoft/guess-number-app"
    dockerImage = ""
  }

  agent any

  stages {

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
      agent{
        docker{
          image: bitnami/kubectl:latest
          args: '-u root:root'
        }
      }
      environment {
        PROJECT_ID = 'gke-guess-number-app'
        CLUSTER_NAME = 'gke-guess-number-game'
        LOCATION = 'us-central1-c'
        CREDENTIALS_ID = 'gke-deployer-credentials'
        NAMESPACE = 'default'
      }
      steps {
        step([$class: 'KubernetesEngineBuilder',
          projectId: env.PROJECT_ID,
          clusterName: env.CLUSTER_NAME,
          location: env.LOCATION,
          manifestPattern: 'k8s/',
          credentialsId: env.CREDENTIALS_ID,
          verifyDeployments: true
        ])
      }
    }
  }

}