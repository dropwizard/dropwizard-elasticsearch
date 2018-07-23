#!groovy
@Library('gini-shared-library@master') _


pipeline {
    agent {
        label 'base'
    }

    environment {
            PROJECT_NAME = getProjectName()
        }

    options {
        gitLabConnection('git.i.gini.net')
    }

    stages {
        stage('Build and test') {
            steps {
                updateGitlabCommitStatus name: 'pipeline', state: 'running'
                sh "./gradlew -g=/efs/${PROJECT_NAME} -I /efs/init.gradle -Pbranch=${env.GIT_BRANCH} -PbuildNumber=${env.BUILD_NUMBER} clean build testReport jacocoTestReport jacocoSumTestReport"
            }
            post {
                always {
                    junit '**/test-results/**/*.xml'
                    publishHTML(target: [reportDir  : 'build/reports/allTests/',
                                         reportFiles: 'index.html',
                                         reportName : 'Test results'])
                    publishHTML(target: [reportDir  : 'build/reports/jacoco/jacocoSumTestReport/html/',
                                         reportFiles: 'index.html',
                                         reportName : 'Jacoco Code Coverage'])
                }
                failure {
                    updateGitlabCommitStatus name: 'Build and test', state: 'failed'
                }
                success {
                    updateGitlabCommitStatus name: 'Build and test', state: 'success'
                }
            }
        }
    }
    post {
        failure {
            updateGitlabCommitStatus name: 'pipeline', state: 'failed'
        }
        success {
            updateGitlabCommitStatus name: 'pipeline', state: 'success'
        }
    }
}
