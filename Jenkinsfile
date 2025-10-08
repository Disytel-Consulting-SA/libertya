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

        stage('Exportar a servidor de releases - DEV') {
            when {
                branch 'dev'
            }
            steps {
                script {
                    echo "📤 Exportando a servidor de releases (DEV)..."
                    
                    def archivo = "${WORKDIR}/install_export/ServidorOXP_V22.0.zip"
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

        stage('Generar Release - MASTER') {
            when {
                branch 'master'
            }
            steps {
                script {
                    echo "🚀 Generando release..."
                    
                    def archivo = "${WORKDIR}/install_export/ServidorOXP_V22.0.zip"
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