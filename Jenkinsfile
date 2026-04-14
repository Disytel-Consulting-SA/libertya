pipeline {
    agent any

    tools {
        jdk 'java-11-openjdk-amd64'
    }

    environment {
        WORKDIR = "${env.WORKSPACE}"
        JAVA_HOME = "${tool 'java-11-openjdk-amd64'}"
        PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
        OXP_HOME = "${WORKDIR}/ServidorOXP"
        INSTALACION_EXPORT = "${WORKDIR}/install_export"
        ROOT_OXP = "${WORKDIR}"
        
        // Configuración dinámica según rama
        IS_DEV = "${env.BRANCH_NAME == 'dev'}"
        IS_MASTER = "${env.BRANCH_NAME == 'master'}"
        
        REPORTS_DIR = "/var/reportes"
        
        // Metadata
        DEVINFO_FILE_LOCATION = "${WORKDIR}/libertya/data/core/upgrade_from_22.0"
        DEVINFO_FILE = "${DEVINFO_FILE_LOCATION}/devinfo.properties"

        DB_NAME = 'libertya_test'
        DB_USER = 'libertya'
        DB_PASS = 'libertya'
        DB_PORT = '5434'

        // Deploy DEV (testing)
        DEPLOY_ENABLED_DEV = 'true'
        REMOTE_OXP_HOME = '/ServidorOXP'
        REMOTE_SERVICE_NAME = 'libertyad'
        REMOTE_APP_USER = 'libertya'
        REMOTE_APP_GROUP = 'libertya'

        // Instancias DEV (activar/desactivar por instancia)
        DEV_DEPLOY_ENABLE_QA2 = 'false'
        DEV_DEPLOY_HOST_CREDENTIAL_QA2 = 'deploy-qa2-host'
        DEV_DEPLOY_PORT_CREDENTIAL_QA2 = 'deploy-qa2-port'
        DEV_DEPLOY_CREDENTIAL_QA2 = 'deploy-qa2-ssh'

        DEV_DEPLOY_ENABLE_QA = 'true'
        DEV_DEPLOY_HOST_CREDENTIAL_QA = 'deploy-qa-host'
        DEV_DEPLOY_PORT_CREDENTIAL_QA = 'deploy-qa-port'
        DEV_DEPLOY_CREDENTIAL_QA = 'deploy-qa-ssh'

        DEV_DEPLOY_ENABLE_QA3 = 'false'
        DEV_DEPLOY_HOST_CREDENTIAL_QA3 = 'deploy-qa3-host'
        DEV_DEPLOY_PORT_CREDENTIAL_QA3 = 'deploy-qa3-port'
        DEV_DEPLOY_CREDENTIAL_QA3 = 'deploy-qa3-ssh'
    }

    stages {

        stage('Clonar y Compilar Libertya') {
            steps {
                dir('libertya'){
                    git branch: "${env.BRANCH_NAME}", url: 'https://github.com/Disytel-Consulting-SA/libertya.git'

                    script {
                        env.LIBERTYA_COMMIT = sh(
                            script: "git rev-parse --short=8 HEAD",
                            returnStdout: true
                        ).trim()
                        
                        echo "📦 Compilando Libertya desde rama: ${env.BRANCH_NAME}"
                        echo "📌 Commit: ${env.LIBERTYA_COMMIT}"
                    }

                    sh '''
                        echo "==> Listando directorios y archivos"
                        ls -lhst

                        INSTALL_DIR="${WORKDIR}/install"
                        mkdir -p ${INSTALL_DIR}
                        chmod -R u+w ${INSTALL_DIR}

                        echo "==> Habilitando permisos de ejecución..."
                        find . -type f -name "*.sh" -exec chmod +x {} \\;

                        echo "==> Ejecutando script de compilación..."
                        cd utils_dev && ./Compilar.sh
                    '''
                }
            }
        }

        stage('Preparar keystore') {
            steps {
                sh '''
                    mkdir -p ${OXP_HOME}/keystore
                    if [ ! -f ${OXP_HOME}/keystore/myKeystore ]; then
                        keytool -genkey -keyalg RSA \
                            -alias libertya \
                            -dname "CN=Jenkins, OU=CI, O=TuEmpresa, L=Ciudad, ST=Provincia, C=AR" \
                            -keypass libertya \
                            -storepass libertya \
                            -validity 365 \
                            -keystore ${OXP_HOME}/keystore/myKeystore
                    fi
                '''
            }
        }

        stage('Configurar Libertya') {
            steps {
                script {
                    // Usar archivo de configuración según la rama
                    def envFileId = (env.BRANCH_NAME == 'dev') ? 'LibertyaEnvMultibranchDev.properties' : 'LibertyaEnvMultibranchMaster.properties'
                                        
                    configFileProvider([
                        configFile(
                            fileId: envFileId,
                            targetLocation: "${env.OXP_HOME}/LibertyaEnv.properties"
                        )
                    ]) {
                        echo "✅ Archivo ${envFileId} configurado."
                    }

                    echo "Habilitando permisos de ejecución..."
                    def oxpHome = env.OXP_HOME
                    sh "find ${oxpHome} -type f -name \"*.sh\" -exec chmod +x {} \\;"

                    echo 'Ejecutando ConfigurarAuto.sh.'
                    sh "cd ${oxpHome} && ./ConfigurarAuto.sh"
                }
            }
        }

        stage('Limpiar OXPXLib.jar') {
            steps {
                sh '''
                    zip -d ${OXP_HOME}/lib/OXPXLib.jar "org/slf4j/impl/*" "META-INF/INDEX.LIST" || true
                '''
            }
        }

        stage('Clonar lyrestapi') {
            steps {
                dir('lyrestapi'){
                    git branch: 'main', url: 'https://github.com/Disytel-Consulting-SA/lyrestapi.git'

                    script {
                        env.LYRESTAPI_COMMIT = sh(
                            script: "git rev-parse --short=8 HEAD",
                            returnStdout: true
                        ).trim()
                    }
                }
            }
        }

        stage('Ejecutar Tests') {
            tools {
                jdk 'java-8'
            }
            steps {
                dir('lyrestapi') {
                    sh 'java -version'
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean test --info'
                }
            }
        }

        stage('Exportar metadata') {
            steps {
                script {
                    // Usar devinfo según la rama
                    // def devinfoFileId = (env.BRANCH_NAME == 'dev') ? 'dev-devinfo.properties' : 'master-devinfo.properties'
                    def devinfoFileId = 'multibranch-devinfo.properties'
                    
                    configFileProvider([
                        configFile(
                            fileId: devinfoFileId,
                            targetLocation: env.DEVINFO_FILE
                        )
                    ]) {
                        echo "✅ Archivo ${devinfoFileId} configurado."
                    }

                    sh """
                        cd ${OXP_HOME}/utils
                        chmod +x *.sh
                        ./PluginExporter.sh ${DEVINFO_FILE}
                    """
                }
            }
        }

        stage('Inyectar BUILD_INFO.properties') {
            steps {
                script {
                    def artifact = "${WORKDIR}/install_export/ServidorOXP_V25.0.zip"
                    def builtAt = sh(
                        script: "date -u +%Y-%m-%dT%H:%M:%SZ",
                        returnStdout: true
                    ).trim()

                    if (!fileExists(artifact)) {
                        error "No se encontró el artefacto para inyectar BUILD_INFO: ${artifact}"
                    }

                    def buildInfoContent = [
                        "branch=${env.BRANCH_NAME}",
                        "commit=${env.LIBERTYA_COMMIT}",
                        "jenkins_build=${env.BUILD_NUMBER}",
                        "built_at=${builtAt}"
                    ].join('\n') + '\n'

                    def buildInfoFile = "${WORKDIR}/BUILD_INFO.properties"
                    def tempZipDir = "${WORKDIR}/.build_info_zip_${env.BUILD_NUMBER}"

                    writeFile(file: buildInfoFile, text: buildInfoContent)

                    sh """
                        set -eu
                        rm -rf "${tempZipDir}"
                        mkdir -p "${tempZipDir}/ServidorOXP"
                        cp "${buildInfoFile}" "${tempZipDir}/ServidorOXP/BUILD_INFO.properties"
                        cd "${tempZipDir}"
                        zip -q -u "${artifact}" "ServidorOXP/BUILD_INFO.properties"
                        rm -rf "${tempZipDir}"
                    """

                    echo "✅ BUILD_INFO.properties inyectado en ${artifact}"
                }
            }
        }

        stage('Exportar a servidor de releases - DEV') {
            when {
                branch 'dev'
            }
            steps {
                script {
                    echo "📤 Exportando a servidor de releases (DEV)..."
                    
                    def archivo = "${WORKDIR}/install_export/ServidorOXP_V25.0.zip"
                    def destinoPath = "/home/developers/releases/libertya-core/dev"
                    def destinoName = "ServidorOXP25-dev-${env.LIBERTYA_COMMIT}.zip"
                    def metadata = "/tmp/export/*.jar"

                    withCredentials([
                        sshUserPrivateKey(credentialsId: 'releases', keyFileVariable: 'KEYFILE', usernameVariable: 'USER'),
                        string(credentialsId: 'releases-ip', variable: 'SERVER_IP'),
                        string(credentialsId: 'releases-port', variable: 'SERVER_PORT')
                    ]) {
                        sh """
                            echo '==> Copiando archivos al servidor de releases (dev)...'
                            scp -i $KEYFILE -o StrictHostKeyChecking=no -P $SERVER_PORT ${archivo} ${USER}@${SERVER_IP}:${destinoPath}/${destinoName}
                            scp -i $KEYFILE -o StrictHostKeyChecking=no -P $SERVER_PORT ${metadata} ${USER}@${SERVER_IP}:${destinoPath}
                        """
                    }
                }
            }
        }

        stage('Deploy a instancias de testing - DEV') {
            when {
                branch 'dev'
            }
            steps {
                script {
                    if (env.DEPLOY_ENABLED_DEV != 'true') {
                        echo "⏭ Deploy DEV deshabilitado (DEPLOY_ENABLED_DEV=${env.DEPLOY_ENABLED_DEV})."
                        return
                    }

                    def artifact = "${WORKDIR}/install_export/ServidorOXP_V25.0.zip"
                    def deployScript = "${WORKDIR}/libertya/scripts/deploy_remote.sh"
                    def remoteOxpHome = env.REMOTE_OXP_HOME
                    def remoteServiceName = env.REMOTE_SERVICE_NAME
                    def remoteAppUser = env.REMOTE_APP_USER
                    def remoteAppGroup = env.REMOTE_APP_GROUP
                    def targetConfigs = [
                        [
                            name: 'qa2',
                            enabled: env.DEV_DEPLOY_ENABLE_QA2,
                            hostCredential: env.DEV_DEPLOY_HOST_CREDENTIAL_QA2,
                            portCredential: env.DEV_DEPLOY_PORT_CREDENTIAL_QA2,
                            credential: env.DEV_DEPLOY_CREDENTIAL_QA2
                        ],
                        [
                            name: 'qa',
                            enabled: env.DEV_DEPLOY_ENABLE_QA,
                            hostCredential: env.DEV_DEPLOY_HOST_CREDENTIAL_QA,
                            portCredential: env.DEV_DEPLOY_PORT_CREDENTIAL_QA,
                            credential: env.DEV_DEPLOY_CREDENTIAL_QA
                        ],
                        [
                            name: 'qa3',
                            enabled: env.DEV_DEPLOY_ENABLE_QA3,
                            hostCredential: env.DEV_DEPLOY_HOST_CREDENTIAL_QA3,
                            portCredential: env.DEV_DEPLOY_PORT_CREDENTIAL_QA3,
                            credential: env.DEV_DEPLOY_CREDENTIAL_QA3
                        ]
                    ]

                    if (!fileExists(artifact)) {
                        error "No se encontró el artefacto a desplegar: ${artifact}"
                    }
                    if (!fileExists(deployScript)) {
                        error "No se encontró script de deploy: ${deployScript}"
                    }

                    def enabledTargets = targetConfigs.findAll { it.enabled == 'true' }
                    if (enabledTargets.isEmpty()) {
                        echo "⏭ No hay instancias DEV habilitadas para deploy."
                        return
                    }

                    enabledTargets.each { target ->
                        if (!target.credential?.trim()) {
                            error "La instancia '${target.name}' está habilitada pero no tiene credential configurada."
                        }
                        if (!target.hostCredential?.trim()) {
                            error "La instancia '${target.name}' está habilitada pero no tiene credencial de host configurada."
                        }
                        if (!target.portCredential?.trim()) {
                            error "La instancia '${target.name}' está habilitada pero no tiene credencial de puerto SSH configurada."
                        }

                        withCredentials([
                            string(credentialsId: target.hostCredential, variable: 'TARGET_HOST'),
                            string(credentialsId: target.portCredential, variable: 'TARGET_PORT'),
                            sshUserPrivateKey(
                                credentialsId: target.credential,
                                keyFileVariable: 'DEPLOY_KEYFILE',
                                usernameVariable: 'DEPLOY_USER'
                            )
                        ]) {
                            def remoteZip = "/tmp/ServidorOXP25-dev-${target.name}-${env.BUILD_NUMBER}-${env.LIBERTYA_COMMIT}.zip"
                            echo "🚚 Desplegando ${artifact} en ${target.name}"

                            withEnv([
                                "DEPLOY_ARTIFACT=${artifact}",
                                "DEPLOY_SCRIPT=${deployScript}",
                                "DEPLOY_REMOTE_ZIP=${remoteZip}",
                                "DEPLOY_REMOTE_OXP_HOME=${remoteOxpHome}",
                                "DEPLOY_REMOTE_SERVICE_NAME=${remoteServiceName}",
                                "DEPLOY_REMOTE_APP_USER=${remoteAppUser}",
                                "DEPLOY_REMOTE_APP_GROUP=${remoteAppGroup}",
                                "DEPLOY_TARGET_NAME=${target.name}"
                            ]) {
                                sh '''
                                    set -eu

                                    if [ -z "${TARGET_HOST:-}" ]; then
                                        echo "La credencial de host para '${DEPLOY_TARGET_NAME}' está vacía."
                                        exit 1
                                    fi
                                    if [ -z "${TARGET_PORT:-}" ]; then
                                        echo "La credencial de puerto para '${DEPLOY_TARGET_NAME}' está vacía."
                                        exit 1
                                    fi
                                    if [ -z "${DEPLOY_USER:-}" ]; then
                                        echo "La credencial SSH para '${DEPLOY_TARGET_NAME}' no expuso usuario."
                                        exit 1
                                    fi

                                    scp -i $DEPLOY_KEYFILE -o StrictHostKeyChecking=no -P $TARGET_PORT $DEPLOY_ARTIFACT $DEPLOY_USER@$TARGET_HOST:$DEPLOY_REMOTE_ZIP
                                    ssh -i $DEPLOY_KEYFILE -o StrictHostKeyChecking=no -p $TARGET_PORT $DEPLOY_USER@$TARGET_HOST \
                                        "OXP_HOME='$DEPLOY_REMOTE_OXP_HOME' SERVICE_NAME='$DEPLOY_REMOTE_SERVICE_NAME' APP_USER='$DEPLOY_REMOTE_APP_USER' APP_GROUP='$DEPLOY_REMOTE_APP_GROUP' bash -s -- '$DEPLOY_REMOTE_ZIP'" < $DEPLOY_SCRIPT
                                '''
                            }
                        }
                    }
                }
            }
        }

        stage('Generar Release - MASTER') {
            when {
                branch 'master'
            }
            steps {
                script {
                    echo "🚀 Generando release..."
                    
                    def archivo = "${WORKDIR}/install_export/ServidorOXP_V25.0.zip"
                    def destinoPath = "/home/developers/releases/libertya-core/master"
                    def destinoName = "ServidorOXP25-release-${env.LIBERTYA_COMMIT}.zip"
                    def metadata = "/tmp/export/*.jar"

                    withCredentials([
                        sshUserPrivateKey(credentialsId: 'releases', keyFileVariable: 'KEYFILE', usernameVariable: 'USER'),
                        string(credentialsId: 'releases-ip', variable: 'SERVER_IP'),
                        string(credentialsId: 'releases-port', variable: 'SERVER_PORT')
                    ]) {
                        sh """
                            echo '==> Copiando release a servidor...'
                            scp -i $KEYFILE -o StrictHostKeyChecking=no -P $SERVER_PORT ${archivo} ${USER}@${SERVER_IP}:${destinoPath}/${destinoName}
                            scp -i $KEYFILE -o StrictHostKeyChecking=no -P $SERVER_PORT ${metadata} ${USER}@${SERVER_IP}:${destinoPath}
                            
                            echo '==> Creando symlink a latest...'
                            ssh -i $KEYFILE -o StrictHostKeyChecking=no -p $SERVER_PORT ${USER}@${SERVER_IP} \
                                "cd ${destinoPath} && ln -sf ${destinoName} ServidorOXP25-latest.zip"
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // Configurar reportes
                sh "mkdir -p ${REPORTS_DIR}"
                sh "cp -r ${WORKDIR}/lyrestapi/build/reports/tests/test/* ${REPORTS_DIR}"
                sh "chmod -R o+r ${REPORTS_DIR}"
                sh "find ${REPORTS_DIR} -type d -exec chmod o+x {} \\;"

                // Enviar email con configuración según rama
                def fechaHora = new Date().format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone('America/Argentina/Buenos_Aires'))
                def commitLibertya = "${env.LIBERTYA_COMMIT}"
                def commitLyrestapi = "${env.LYRESTAPI_COMMIT}"
                def status = currentBuild.currentResult
                def colorStatus = (status == 'SUCCESS') ? '#28a745' : '#dc3545'
                def emoji = (status == 'SUCCESS') ? '✅' : '❌'
                def branchName = "${env.BRANCH_NAME}"
                def branchColor = (branchName == 'master') ? '#dc3545' : '#0366d6'
                def branchLabel = (branchName == 'master') ? '🏷️ RELEASE' : '🔧 DEV'
                
                withCredentials([string(credentialsId: 'report-url', variable: 'REPORT_URL')]) {
                    def linkReport = "${env.REPORT_URL}"

                    def mensaje = """
                    <html>
                        <body style="font-family: Arial, sans-serif; background: #f6f8fa; padding: 30px;">
                            <div style="background: #fff; border-radius: 12px; box-shadow: 0 4px 16px #0001; padding: 24px; max-width: 540px; margin: 0 auto;">
                                <h2 style="color:#2277BB; margin-top:0;">${emoji} Jenkins - Libertya Core</h2>
                                <div style="background: ${branchColor}22; border-left: 4px solid ${branchColor}; padding: 12px; margin-bottom: 16px; border-radius: 4px;">
                                    <strong style="color: ${branchColor};">${branchLabel}</strong>
                                    <span style="color: #666; margin-left: 8px;">Rama: ${branchName}</span>
                                </div>
                                <p>Resumen del último build ejecutado:</p>
                                <hr style="border:none; border-top:1px solid #eee; margin:16px 0;">
                                <table style="width:100%; font-size: 15px;">
                                    <tr>
                                        <td><b>Estado:</b></td>
                                        <td><span style="color:${colorStatus}; font-weight:bold;">${status}</span></td>
                                    </tr>
                                    <tr>
                                        <td><b>Fecha y hora:</b></td>
                                        <td>${fechaHora}</td>
                                    </tr>
                                    <tr>
                                        <td><b>Commit Libertya:</b></td>
                                        <td><code style="font-size:13px;">${commitLibertya}</code></td>
                                    </tr>
                                    <tr>
                                        <td><b>Commit lyrestapi:</b></td>
                                        <td><code style="font-size:13px;">${commitLyrestapi}</code></td>
                                    </tr>
                                    <tr>
                                        <td><b>Ver reporte:</b></td>
                                        <td><a href="${linkReport}" style="color:#2277BB;">Tests completos</a></td>
                                    </tr>
                                </table>
                                <p style="font-size: 12px; color: #aaa; text-align: right; margin-top: 20px;">Jenkins CI/CD</p>
                            </div>
                        </body>
                    </html>
                    """

                    emailext (
                        from: 'Jenkins <lby.ic@libertya.org>',
                        subject: "${emoji} Build ${status} - Libertya Core (${branchName})",
                        body: mensaje,
                        mimeType: 'text/html',
                        to: 'julian.viejo@disytel.net, federico.cristina@disytel.net, ignacio.aita@disytel.net, jorge.dreher@disytel.net'
                    )
                }
            }
        }
    }
}
