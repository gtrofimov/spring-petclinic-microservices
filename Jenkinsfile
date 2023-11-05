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
        app_name = 'petclinic' // top level DTP Project
        ENV_NAME = 'Local PetClinic' // no longer makes sense
        ENCODED_ENV_NAME = URLEncoder.encode(ENV_NAME, 'UTF-8')

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

        // dynamic vars
        BUILD_TIMESTAMP = sh(script: 'date +%Y%m%d%H%M%S', returnStdout: true).trim()
        PUBLIC_IP = sh(script: """curl -s https://httpbin.org/ip | jq -r '.origin'""", returnStdout: true).trim()
        // envId = sh(script: """curl -s -X 'GET' -H 'accept: application/json' -u ${DTP_USER}:${DTP_PASS} ${CTP_URL}/em/api/v3/environments?name=${ENCODED_ENV_NAME}&limit=50&offset=0 | jq -r '.environments[0].id'""", returnStdout: true).trim()
        // buildId = "${app_name}-${BUILD_TIMESTAMP}"


        envId = '32' // need to be dynamically acquired via curl
        covImage='SeleniumTests'
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
                // downlaod the agent.jar and cov-tool
                sh '''
                
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
                }
                // prepare CTP JSON file
                script {
                    // get ctp.json file form CTP
                    sh '''
                        ctp_response=$(curl -s -X 'GET' -H 'accept: application/json' -u ${DTP_USER}:${DTP_PASS} ${CTP_URL}/em/api/v3/environments?name=${ENCODED_ENV_NAME}&limit=50&offset=0)
                        envId=$(echo "$ctp_response" | jq -r '.environments[0].id')
                        echo ${envId}
                        curl -X 'GET' -H 'accept: application/json' -u ${DTP_USER}:${DTP_PASS} ${CTP_URL}/em/api/v3/environments/${envId}/config | jq . > ctp.json
                        cat ctp.json
                        '''

                    // Read in ctp.json file
                    def jsonFile = readFile("ctp.json")
                    def json = new groovy.json.JsonSlurperClassic().parseText(jsonFile)

                    // debug
                    echo "${PUBLIC_IP}"

                    // Update the 'buildId' and 'coverageImages' properties
                    for (service in ARRAY) {
                        echo "service is: ${service}"
                        def matchingComponent = json.components.find { it.instances.find { it.coverage?.dtpProject == service } }
                        if (matchingComponent) {
                            
                            // retain ports
                            def url = new URL(matchingComponent.instances[0].coverage.agentUrl)
                            def originalPort = url.port
                            
                            // Combine PUBLIC_IP with the original port
                            matchingComponent.instances[0].coverage.agentUrl = "http://${PUBLIC_IP}:${originalPort}"
                            matchingComponent.instances[0].coverage.buildId = "${service}-${BUILD_TIMESTAMP}"
                            // matchingComponent.instances[0].coverage.coverageImages = "${functionalCovImage}"
                        } else {
                            echo "Something is NULL!"
                        }
                    }
                    // Write the updated JSON back to the file using writeJSON
                    writeJSON file: "ctp.json", json: json, pretty: 4
                    sh "cat ctp.json"
                }     
                // copy jars
                script {
                    for (service in ARRAY) {
                        sh "cp jtest_agent/agent.jar ${service}/src/test/resources/coverage/agent.jar"
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
                    report.coverage.images=${covImage}
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
                    dtp.project=${app_name}
                    report.dtp.publish=true" > ./jtestcov/jtestcli.properties
                    '''
                

            }
                
        }
        stage('Build') {
            when { equals expected: true, actual: false }
            steps {
                
                // build the binaries
                echo "Building ${env.JOB_NAME}..."
                sh  '''

                    # Build the Maven package
                    mvn clean package -DskipTests=true

                    '''
                
                // scan the binaries
                script {
                    for (service in ARRAY) {
                     // jtest cov
                    sh "java -jar jtestcov/jtestcov.jar \
                        -app ${service}/target/*.jar \
                        -include org/springframework/samples/** \
                        -settings jtestcov/jtestcli.properties \
                        -property dtp.project=${service} \
                        -property report.dtp.publish=true \
                        -property report.coverage.images=${covImage}"
                    }
                }
            }
        }
        stage('Deploy') {
            when { equals expected: true, actual: false }
            steps {
                
                // check running containers
                sh '''
                    docker-compose -f docker-compose-cc.yml down || true
                    sleep 10s
                    '''
                // deploy the project
                sh  '''
                    # Run PetClinic with Jtest coverage agent configured
                    docker-compose -f docker-compose-cc.yml up -d
                    sleep 80s
                    '''

                // Health check coverage agents
                sh '''
                    curl -iv --raw http://localhost:8051/status

                    '''
                // update CTP with yaml script upload
                sh '''
                    # upload yaml file to CTP
                    curl -X 'PUT' -u ${DTP_USER}:${DTP_PASS} \
                        ${CTP_URL}/em/api/v3/environments/${envId}/config \
                        -H 'accept: application/json' \
                        -H 'Content-Type: application/json' \
                        -d @ctp.json
                    '''
            }
        }
        stage('Test') {
            when { equals expected: true, actual: true}
            steps {
                // run Selenium tests
                sh """
                    cd spring-petclinic-selenium-tests
                    mvn clean test -DbaseUrl=http://${PUBLIC_IP}:8099
                    """
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
            archiveArtifacts(artifacts: ''' 
                    **/target/*.jar, 
                    ''',
                fingerprint: true, 
                onlyIfSuccessful: true,
            )
            
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
