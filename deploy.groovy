@Library('Shared')
import com.library.Shared

def shared = new Shared(this)
def versions = ["2.2.8","2.1.4","1.0.2"]
def envs = ["ft1","ft2"]
def repoName = "poc-ci-jenkinsfiles"
def repoOwner = "rummyze"
 
pipeline {
    agent any

    parameters { 
        choice(name: 'APP_VERSION', choices: versions, description: 'app version')
        choice(name: 'ENV', choices: envs, description: 'app version')
        choice(name: 'APP_VERSION1', choices: versions, description: 'app version')    }

    
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
                stage('download service-2') {
                    steps {
                        script {
                            shared.downloadWorldService(params.APP_VERSION1)
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
                stage('deploy service-2') {
                    steps {
                        script {
                            shared.deployWorldService(params.ENV)
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
