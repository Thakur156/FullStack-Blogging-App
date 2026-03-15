pipeline {
    agent any
    
    tools {
        jdk 'jdk17'
        maven 'maven3'
    }
    
   environment {
       SCANNER_HOME= tool 'sonar-scanner'
   }

    stages {
        stage('Git Checkout') {
            steps {
               git branch: 'main', credentialsId: 'git-cred', url: 'https://github.com/Thakur156/FullStack-Blogging-App.git'
            }
        }
        
        stage('Compile') {
            steps {
                sh "mvn compile"
            }
        }
        
        stage('Test') {
            steps {
               sh "mvn test"
            }
        }
        
        stage('Trivy FS Scan') {
            steps {
                sh "trivy fs --format table -o fs.html ."
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                      sh ''' $SCANNER_HOME/bin/sonar-scanner -Dsonar.projectName=Blogging-app \
                      -Dsonar.projectKey=Blogging-app -Dsonar.java.binaries=target'''
                 }
            }
        }
        
        stage('Build') {
            steps {
               sh "mvn package"
             }
        }
        
        stage('Publish Artifacts') {
            steps {
              withMaven(globalMavenSettingsConfig: 'maven-settings', jdk: 'jdk17', maven: 'maven3', traceability: true) {
                       sh "mvn deploy"
                 }
             }
        }
        
        
        stage('Docker Build and Tag') {
            steps {
                script{
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                         sh "docker build -t thakur156/bloggingapp:latest ."
                      }
                }
                
             }
        }
        
        stage('Trivy Image Scan') {
            steps {
                sh "trivy image --format table -o fs.html thakur156/bloggingapp:latest "
            }
        }
        
        
        
        stage('Docker Push Image') {
            steps {
                script{
                    withDockerRegistry(credentialsId: 'docker-cred', toolName: 'docker') {
                         sh "docker push thakur156/bloggingapp:latest "
                      }
                }
                
             }
        }
       
        
        stage('k8-deploy') {
            steps {
                withKubeConfig(caCertificate: '', clusterName: ' devopsshack-cluster', contextName: '', credentialsId: 'k8-creds', namespace: 'webapps', restrictKubeConfigAccess: false, serverUrl: 'https://331BB6F3DF051EF0C79F27A44C7BA9E0.gr7.us-east-1.eks.amazonaws.com') {
                      sh "kubectl apply -f deployment-service.yml"
                      sleep 30
                    }
             }
        } 
        
        
        stage('verify the deployment') {
            steps {
                withKubeConfig(caCertificate: '', clusterName: ' devopsshack-cluster', contextName: '', credentialsId: 'k8-creds', namespace: 'webapps', restrictKubeConfigAccess: false, serverUrl: 'https://331BB6F3DF051EF0C79F27A44C7BA9E0.gr7.us-east-1.eks.amazonaws.com') {
                      sh "kubectl get pods"
                      sh "kubectl get svc"

                    }
             }
        } 


    }


post {
    always {
        script {
            def jobName = env.JOB_NAME
            def buildNumber = env.BUILD_NUMBER
            def pipelineStatus = currentBuild.result ?: 'UNKNOWN'
            def bannerColor = pipelineStatus.toUpperCase() == 'SUCCESS' ? 'green' : 'red'

            def body = """
            <html>
            <body>
                <div style="border: 4px solid ${bannerColor}; padding: 10px;">
                    <h2>${jobName} - Build #${buildNumber}</h2>

                    <div style="background-color: ${bannerColor}; padding: 10px;">
                        <h3 style="color: white;">
                            Pipeline Status : ${pipelineStatus.toUpperCase()}
                        </h3>
                    </div>

                    <p>
                        Check the <a href="${BUILD_URL}">console output</a>.
                    </p>
                </div>
            </body>
            </html>
            """

            emailext(
                subject: "${jobName} - Build #${buildNumber} - ${pipelineStatus.toUpperCase()}",
                body: body,
                to: 'aliisrar156@gmail.com',
                from: 'aliisrar156@gmail.com',
                replyTo: 'aliisrar156@gmail.com',
                mimeType: 'text/html'
            )
        }
    }
}

    
}
