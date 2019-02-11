@Library('Shared')
import com.library.Shared

def shared = new Shared(this)
def envs = ["ft1","ft2"]
def repoName = "poc-ci-jenkinsfiles"
def repoOwner = "rummyze"

pipeline {
    agent any

    parameters { 

        choice(name: 'ENV', choices: envs, description: 'app version')
    }

    
    stages {

        stage("cleanup") {
            steps {
                deleteDir()
                sh "mkdir -p deploy"
                git branch: "master", credentialsId: 'github-key', url: "git@github.com:${repoOwner}/${repoName}.git"
            }
        }
        

        stage('download') {
            parallel {
                stage('download service-1') {
                    steps {
                        script {
                            shared.downloadHelloService(params.APP_VERSION)
                        }
                    }
                }
            }
        }

        stage('deploy') {
            parallel {
                stage('deploy service-1') {
                    steps {
                        script {
                            shared.deployHelloService(params.ENV)
                        }
                    }
                }
            }
        }
    }
    
    options {
        buildDiscarder(logRotator(numToKeepStr: '3'))
    }

}
