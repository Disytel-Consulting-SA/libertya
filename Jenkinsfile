pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/Disytel-Consulting-SA/libertya.git'
            }
        }
        stage('Build') {
            steps {
                echo 'Compilando...'
            }
        }
        stage('Test') {
            steps {
                echo 'Ejecutando tests...'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Desplegando...'
		sh 'ls -lhst *'
            }
        }
    }
}
