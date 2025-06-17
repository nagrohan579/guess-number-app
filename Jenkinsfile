pipeline {
  agent any

  environment {
    dockerimagename = "rohannagcalsoft/guess-number-app"
    PROJECT_ID      = 'gke-guess-number-app'
    CLUSTER_NAME    = 'gke-guess-number-game'
    LOCATION        = 'us-central1-c'
    CREDENTIALS_ID  = 'gke-deployer-credentials'
    NAMESPACE       = 'default'
  }

  stages {
    stage('Maven Build') {
      steps {
        sh 'mvn clean package'
      }
    }

    stage('Build image') {
      steps {
        script {
          dockerImage = docker.build(dockerimagename)
        }
      }
    }

    stage('Pushing Image') {
      environment {
        registryCredential = 'dockerhub-credentials'
      }
      steps {
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
          // Fetch GKE API server endpoint
          env.SERVER_URL = sh(
            script: "gcloud container clusters describe ${CLUSTER_NAME} --zone ${LOCATION} --project ${PROJECT_ID} --format='value(endpoint)'",
            returnStdout: true
          ).trim()
        }
        // Use withKubeConfig inside declarative steps
        withKubeConfig([
          credentialsId: CREDENTIALS_ID,
          serverUrl: "https://${env.SERVER_URL}",
          clusterName: CLUSTER_NAME,
          namespace: NAMESPACE
        ]) {
          // Ensure kubectl is present if not installed on agent
          sh 'curl -LO "https://dl.k8s.io/release/v1.20.5/bin/linux/amd64/kubectl"'
          sh 'chmod u+x kubectl'
          // Deploy both deployment.yaml and service.yaml
          sh './kubectl apply -f k8s/'
        }
      }
    }
  }
}
