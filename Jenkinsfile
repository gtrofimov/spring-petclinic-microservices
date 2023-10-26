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
    }
    environment {
        // App Settings
        app_name = 'petclinic' //DTP Project

        // Parasoft Licenses
        ls_url = "${LS_URL}" //https\://dtp:8443
        ls_user = "${LS_USER}" //admin
        ls_pass = "${LS_PASS}"

        // Parasoft Common Settings
        dtp_url = "${DTP_URL}" //https://dtp:8443
        dtp_user = "${DTP_USER}" //admin
        dtp_pass = "${DTP_PASS}"
        ctp_url = "${CTP_URL}"

        // dtp_publish="${DTP_PUBLISH}" //false
        buildId = "${app_name}-${BUILD_TIMESTAMP}"

        // ARRAY = ["spring-petclinic-api-gateway", "spring-petclinic-vets-service", "spring-petclinic-visits-service", "spring-petclinic-customers-service"]

    }
    stages {
        stage('Set Up') {
            steps {
                // Clean before build
                cleanWs()

                // Checkout project
                checkout scm

                // downlaod the agent.jar and cov-tool
                sh '''
                    echo ${ARRAY}
                    curl -LO -u ${DTP_USER}:${DTP_PASS} ${CTP_URL}/em/coverageagent/java_agent_coverage.zip
                    unzip java_agent_coverage.zip
                
                    '''
                // define services
                script {
                    ARRAY = [
                        "spring-petclinic-api-gateway", 
                        "spring-petclinic-vets-service", 
                        "spring-petclinic-visits-service", 
                        "spring-petclinic-customers-service"
                        ]

                    for (dir in ARRAY) {
                        sh "echo ${dir}"
                    }
                }

                // copy jars
                script {
                    for (dir in ARRAY) {
                        sh "cp jtest_agent/agent.jar ${dir}/src/test/resources/coverage/agent.jar"
                    }
                }
                
                // set up configs
                sh '''
                    # Set Up and write .properties file
                    echo $"
                    parasoft.eula.accepted=true
                    jtest.license.use_network=true
                    jtest.license.network.edition=custom_edition
                    jtest.license.custom_edition_features=Jtest, Static Analysis, Flow Analysis, OWASP Rules, CWE Rules, PCI DSS Rules, DISA STIG Rules, Security Rules, Automation, Desktop Command Line, DTP Publish, Coverage, Unit Test, Unit Test Bulk Creation, Unit Test Tier 1, Unit Test Tier 2, Unit Test Tier 3, Unit Test Tier 4, Unit Test Spring Framework, Change Based Testing
                    license.network.use.specified.server=true
                    license.network.auth.enabled=true
                    license.network.url=${LS_URL}
                    license.network.user=${LS_USER}
                    license.network.password=${LS_PASS}

                    # report.associations=false
                    # report.coverage.images=${unitCovImage}
                    # report.scontrol=full
                    # scope.local=true
                    # scope.scontrol=true
                    # scope.xmlmap=false
                    
                    # scontrol.git.exec=git
                    # scontrol.rep1.git.branch=main
                    # scontrol.rep1.git.url=${project_repo}
                    # scontrol.rep1.type=git

                    build.id=${buildId}
                    session.tag=${jtestSessionTag}
                    dtp.url=${DTP_URL}
                    dtp.user=${DTP_USER}
                    dtp.password=${DTP_PASS}
                    dtp.project=${project_name}" > ./jtestcov/jtestcli.properties
                    '''

            }
                
        }
        stage('Build') {
            when { equals expected: true, actual: true }
            steps {
                
                // build the binaries
                echo "Building ${env.JOB_NAME}..."
                sh  '''

                    # Build the Maven package
                    mvn clean package -DskipTests=true

                    '''
                
                // scan the binaries
                script {
                    for (dir in ARRAY) {
                     // jtest cov
                    sh '''
                        java -jar jtestcov/jtestcov.jar \
                        -soatest \
                        -app "${dir}/target/*.jar" \
                        -include org/springframework/samples/** \
                        -settings jtestcov/jtestcli.properties                    
                        
                        '''
                    }
                }
            }
        }
        stage('Deploy-CodeCoverage') {
            when { equals expected: true, actual: true }
            steps {
                
                // generate static cov file
                // interate through services
                
                
                sh '''
                    java -jar jtestcov/jtestcov.jar \
                    -soatest \
                    -app spring-petclinic-api-gateway/target/*.jar \
                    -include org/springframework/samples/** \
                    -settings jtestcov/jtestcli.properties                    
                    
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
                    sleep 60s
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
            sh  '''
                echo "cleaning up..."
                rm -rf "jtest_agent"
                rm -rf "jtestcov"
                
                # rm -rf ".jtest/cache"
                # rm -rf "*/*/*/.jtest/cache"
                '''
        }
    }
}
