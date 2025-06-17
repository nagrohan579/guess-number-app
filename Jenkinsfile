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
      steps {
        step(withKubeConfig([credentialsId: env.CREDENTIALS_ID,
          clusterName: env.CLUSTER_NAME,
          namespace: env.NAMESPACE
        ]){
          sh '''
            echo "Deployment started ..."
            curl -LO "https://storage.googleapis.com/kubernetes-release/release/v1.20.5/bin/linux/amd64/kubectl"
            chmod u+x ./kubectl
            ./kubectl apply -f deployment.yaml
            ./kubectl apply -f service.yaml
            echo "Deployment completed successfully!"
          '''
        }
        )
      }
    }
  }

}