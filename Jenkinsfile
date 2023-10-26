pipeline {
    agent any
    tools {
        maven 'maven'
        jdk 'JDK 17'
    }
    options {
        // This is required if you want to clean before build
        skipDefaultCheckout(true)
    }    
    environment {
        // App Settings
        app_name="PetClinic" //DTP Project

        // Parasoft Licenses
        ls_url="${LS_URL}" //https\://dtp:8443
        ls_user="${LS_USER}" //admin
        ls_pass="${LS_PASS}"
        
        // Parasoft Common Settings
        dtp_url="${DTP_URL}" //https://dtp:8443
        dtp_user="${DTP_USER}" //admin
        dtp_pass="${DTP_PASS}"
        // dtp_publish="${DTP_PUBLISH}" //false
        buildId="${app_name}-${BUILD_TIMESTAMP}"

    }
    stages {        
        stage('Build') {
            steps {
                // Clean before build
                cleanWs()

                // Checkout project
                checkout scm
                
                // build the project                
                echo "Building ${env.JOB_NAME}..."
                sh  '''

                    # Build the Maven package
                    # mvn clean package
                                        
                    '''
                }
            }
        stage('Deploy-CodeCoverage') {
            steps {
                // downlaod the agent.jar and cov-tool
                // unzip
                // copy in to the coverage folder
                sh '''
                    cp docker/coverage/agent.jar spring-petclinic-customers-service/src/test/resources/coverage/agent.jar
                    cp docker/coverage/agent.jar spring-petclinic-vets-service/src/test/resources/coverage/agent.jar
                    cp docker/coverage/agent.jar spring-petclinic-visits-service/src/test/resources/coverage/agent.jar
                    '''
                // check running containers
                sh '''
                    docker-compose -f docker-compose-cc.yml down || true
                    sleep 10s
                    '''
                // deploy the project
                sh  '''
                    # Run PetClinic with Jtest coverage agent configured
                    docker-compose -f docker-compose-cc.yml up -d
                    '''

                // Health check coverage agents
                sh '''
                    
                    '''
                // update CTP with yaml script upload
                sh '''
                    # Set Up and write .properties file
                    # TODO

                    # upload yaml file to CTP
                    # TODO
                    '''
            }
        }
    }            
    post {
        // Clean after build
        always {
            //sh 'docker container stop ${app_name}'
            //sh 'docker container rm ${app_name}'
            //sh 'docker image prune -f'
            // delete Jtest Cache
        }
    }
}