def ARRAY

pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'JDK 17'
    }
    options {
        // This is required if you want to clean before build
        skipDefaultCheckout(true)
        
        // Ontly keep 5 jobs history and 2 jobs artifacts
        buildDiscarder(logRotator(numToKeepStr: '5', artifactNumToKeepStr: '2'))
    }
    parameters {
        string(name: 'LS_URL', description: 'Parasoft LS URL')
        string(name: 'CTP_URL', description: 'Parasoft CTP URL')
        string(name: 'DTP_URL', description: 'Parasoft DTP URL')
        string(name: 'DTP_USER', description: 'Parasoft DTP Username')
        string(name: 'DTP_PASS', description: 'Parasoft DTP Password')
        //string(name: 'ENV_NAME', defaultValue: 'Local PetClinic', description: 'Environment Name')
        //string(name: 'SERVICES_ARRAY', defaultValue: '', description: 'Array of services')
        //string(name: 'PORTS', defaultValue: '', description: 'Ports')

        // Add other parameters as needed
    }
    environment {
        // App Settings
        app_name = 'spring-petclinic-microservices' // top level DTP Project
//        ENV_NAME = 'Local PetClinic' // no longer makes sense
//        ENCODED_ENV_NAME = URLEncoder.encode(ENV_NAME, 'UTF-8')
//
//        // Parasoft Licenses
//        ls_url = "${LS_URL}" //https\://dtp:8443
//        ls_user = "${LS_USER}" //admin
//        ls_pass = "${LS_PASS}"
//
//        // Parasoft Common Settings
//        dtp_url = "${DTP_URL}" //https://dtp:8443
//        dtp_user = "${DTP_USER}" //admin
//        dtp_pass = "${DTP_PASS}"
//        ctp_url = "${CTP_URL}"
//
//        // dtp_publish="${DTP_PUBLISH}" //false
//
//        // dynamic vars
//        BUILD_TIMESTAMP = sh(script: 'date +%Y%m%d%H%M%S', returnStdout: true).trim()
//        PUBLIC_IP = sh(script: """curl -s https://httpbin.org/ip | jq -r '.origin'""", returnStdout: true).trim()
//        // envId = sh(script: """curl -s -X 'GET' -H 'accept: application/json' -u ${DTP_USER}:${DTP_PASS} ${CTP_URL}/em/api/v3/environments?name=${ENCODED_ENV_NAME}&limit=50&offset=0 | jq -r '.environments[0].id'""", returnStdout: true).trim()
//        // buildId = "${app_name}-${BUILD_TIMESTAMP}"
//
//
//        envId = '32' // need to be dynamically acquired via curl
//        covImage='SeleniumTests'
//        sessionTag='jenkins-build'

    }

    stages {
        stage('Set Up') {
            steps {
                // Clean before build
                cleanWs()

                // Checkout project
                checkout scm
                
                // set GID
                script {
                    env.GID = sh(script: 'id -g jenkins', returnStdout: true).trim()
                }                
            }
        }
        stage('Build') {
            when { equals expected: true, actual: true }
            steps {
                
                // Params?
                build job: 'Petclinic-build', parameters: []

            }
        }
        stage('Deploy') {
            when { equals expected: true, actual: true }
            steps {
                      
                // LS_URL, CTP_URL, DTP_URL, DTP_USER, DTP_PASS, Env Name, 
                // Services ARRAY
                // Ports
    
                build job: 'Petclinic-deploy', parameters: [
                    string(name: 'LS_URL', value: params.LS_URL),
                    string(name: 'CTP_URL', value: params.CTP_URL),
                    string(name: 'DTP_URL', value: params.DTP_URL),
                    string(name: 'DTP_USER', value: params.DTP_USER),
                    string(name: 'DTP_PASS', value: params.DTP_PASS),
                    //string(name: 'ENV_NAME', value: params.ENV_NAME),
                    //string(name: 'SERVICES_ARRAY', value: params.SERVICES_ARRAY),
                    //string(name: 'PORTS', value: params.PORTS)
                    // Add other parameters as needed
                ]
            
            }
        }
        stage('Test') {
            when { equals expected: true, actual: true}
            steps {
                // run regression suite
                // Params: GRID_URL, APP_URL, CTP_URL, ENV_NAME, BaselineId             
                build job: 'Petclinic-test', parameters: [
                    string(name: 'LS_URL', value: params.LS_URL),
                          string(name: 'CTP_URL', value: params.CTP_URL),
                          string(name: 'DTP_URL', value: params.DTP_URL),
                          string(name: 'DTP_USER', value: params.DTP_USER),
                          password(name: 'DTP_PASS', value: params.DTP_PASS),
                          //string(name: 'ENV_NAME', value: params.ENV_NAME),
                          //string(name: 'SERVICES_ARRAY', value: params.SERVICES_ARRAY),
                          //string(name: 'PORTS', value: params.PORTS)
                          // Add other parameters as needed
                ]

            }
        }
    }
    post {
        // Clean after build
        always {
            // delete Cov-tool stuff
            sh  '''
                echo "cleaning up..."
                rm -rf "jtest_agent"
                rm -rf "jtestcov"
                '''
        }
    }
}
