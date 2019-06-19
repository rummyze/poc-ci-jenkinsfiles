@Library('Shared')
import com.library.Shared

def shared = new Shared(this)

pipeline {
 agent any
 environment {
  dotnet = 'path\to\dotnet.exe'
 }
 parameters { 
        string(name: 'customersapiapp', choices: envs, description: 'app version')
        string(name: 'customersmvcapp', choices: versions, description: 'app version')
        string(name: 'res_group', choices: versions, description: 'app version')    }
 stages {
  stage('Checkout') {
   steps {
    git credentialsId: 'userId', url: 'https://github.com/rummyze/netapp', branch: 'master'
   }
  }
  stage('Restore PACKAGES') {
   steps {
    bat "dotnet restore --configfile NuGet.Config"
   }
  }
  stage('Clean') {
   steps {
    bat 'dotnet clean'
   }
  }
  stage('Build') {
   steps {
    bat 'dotnet build --configuration Release'
   }
  }
  stage('Pack') {
   steps {
    bat 'dotnet pack --no-build --output nupkgs'
   }
  }
  stage('Publish') {
   steps {
    bat "dotnet nuget push **\\nupkgs\\*.nupkg -k yourApiKey -s            http://myserver/artifactory/api/nuget/nuget-internal-stable/com/sample"
   }
  }
  stage('deploy') {
        azureWebAppPublish azureCredentialsId: params.azure_cred_id,
            resourceGroup: params.res_group, appName: params.customersapiapp, sourceDirectory: "src/CustomersAPI/bin/Release/netcoreapp2.1/publish/"
        azureWebAppPublish azureCredentialsId: params.azure_cred_id,
            resourceGroup: params.res_group, appName: params.customersmvcapp, sourceDirectory: "src/CustomersMVC/bin/Release/netcoreapp2.1/publish/"
   }
 }
}
