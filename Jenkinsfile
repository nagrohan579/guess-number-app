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
      environment {
        PROJECT_ID = 'gke-guess-number-app'
        CLUSTER_NAME = 'gke-guess-number-game'
        LOCATION = 'us-central1-c'
        CREDENTIALS_ID = 'gke-deployer-credentials'
        NAMESPACE = 'default'
      }
      steps{
        step(
          script{
            env.SERVER_URL = sh(
              script: "gcloud container clusters describe ${env.CLUSTER_NAME} --zone ${env.LOCATION} --project ${env.PROJECT_ID} --format='value(endpoint)'",
              returnStdout: true
            ).trim()
          }

          withKubeConfig([credentialsId: env.CREDENTIALS_ID,
          serverUrl: "https://${env.SERVER_URL}",
          clusterName: env.CLUSTER_NAME,
          namespace: env.NAMESPACE
          ]) {
          sh 'curl -LO "https://storage.googleapis.com/kubernetes-release/release/v1.20.5/bin/linux/amd64/kubectl"'  
          sh 'chmod u+x ./kubectl'  
          sh './kubectl apply -f k8s/'
          }
        )
      }
    }
  }

}