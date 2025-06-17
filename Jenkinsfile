pipeline {
  agent any

  environment {
    dockerimagename = "rohannagcalsoft/guess-number-app"
    PROJECT_ID      = 'gke-guess-number-app'
    CLUSTER_NAME    = 'gke-guess-number-game'
    LOCATION        = 'us-central1-c'
    GCLOUD_CREDS    = credentials('gcloud-creds')   // your service account JSON
    DOCKER_CREDS    = 'dockerhub-credentials'       // your DockerHub creds ID
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
      steps {
        script {
          docker.withRegistry('https://registry.hub.docker.com', DOCKER_CREDS) {
            dockerImage.push("latest")
          }
        }
      }
    }

    stage('Deploying App to Kubernetes') {
      steps {
        script {
          // Activate service account using the injected JSON file
          sh """
            gcloud auth activate-service-account --key-file=$GCLOUD_CREDS
            gcloud config set project $PROJECT_ID
            gcloud config set compute/zone $LOCATION
          """

          // Get cluster credentials locally (sets up kubectl config)
          sh "gcloud container clusters get-credentials $CLUSTER_NAME --zone $LOCATION --project $PROJECT_ID"

          // Ensure kubectl is present (or pre-install on agent image)
          sh 'curl -LO "https://dl.k8s.io/release/v1.20.5/bin/linux/amd64/kubectl"'
          sh 'chmod u+x kubectl'

          // Deploy app manifests
          sh './kubectl apply -f k8s/'
        }
      }
    }
  }
}
