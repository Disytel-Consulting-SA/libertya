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
		LIBERTYA_ENV_FILE_ID = 'LibertyaEnvDev.properties'
        
		REPORTS_DIR = "/var/reportes"
		BRANCH_NAME = 'dev'

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
 
                    // Guardar commit de LY CORE
                    script {
                        env.LIBERTYA_COMMIT = sh(
                            script: "git rev-parse --short=8 HEAD",
                            returnStdout: true
                        ).trim()
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
                    configFileProvider([
                        configFile(
                            fileId: "${env.LIBERTYA_ENV_FILE_ID}",
                            targetLocation: "${env.OXP_HOME}/LibertyaEnv.properties"
                        )
                    ]) {
                        echo '✅ Archivo LibertyaEnv.properties configurado.'
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

                    // Guardar commit de Lyrestapi
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

    }

    post {
        always {
            script {
                // Configurar reportes
                sh "mkdir -p ${REPORTS_DIR}"
                sh "cp -r ${WORKDIR}/lyrestapi/build/reports/tests/test/* ${REPORTS_DIR}"
                sh "chmod -R o+r ${REPORTS_DIR}"
                sh "find ${REPORTS_DIR} -type d -exec chmod o+x {} \\;"

                // Enviar email
                def fechaHora = new Date().format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone('America/Argentina/Buenos_Aires'))
                def commitLibertya = "${env.LIBERTYA_COMMIT}"
                def commitLyrestapi = "${env.LYRESTAPI_COMMIT}"
                def status = currentBuild.currentResult
                def colorStatus = (status == 'SUCCESS') ? '#28a745' : '#dc3545'
                def emoji = (status == 'SUCCESS') ? '✅' : '❌'
                withCredentials([string(credentialsId: 'report-url', variable: 'REPORT_URL')]) {
                    def linkReport = "${env.REPORT_URL}"

                    def mensaje = """
                    <html>
                        <body style="font-family: Arial, sans-serif; background: #f6f8fa; padding: 30px;">
                            <div style="background: #fff; border-radius: 12px; box-shadow: 0 4px 16px #0001; padding: 24px; max-width: 540px; margin: 0 auto;">
                                <h2 style="color:#2277BB; margin-top:0;">${emoji} Notificación de Jenkins - Libertya Core</h2>
                                <p>¡Hola equipo! Les dejamos el resumen del último build ejecutado.</p>
                                <hr style="border:none; border-top:1px solid #eee; margin:16px 0;">
                                <table style="width:100%; font-size: 15px;">
                                    <tr>
                                        <td><b>Estado de los tests:</b></td>
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
                                        <td><b>Ver reporte completo:</b></td>
                                        <td><a href="${linkReport}" style="color:#2277BB;">${linkReport}</a></td>
                                    </tr>
                                </table>
                                <br>
                                <ul style="color:#666; font-size:14px;">
                                    <li>Notificación automática del pipeline Jenkins</li>
                                </ul>
                                <p style="font-size: 12px; color: #aaa; text-align: right;">Este mensaje fue generado automáticamente por Jenkins.</p>
                            </div>
                        </body>
                    </html>
                    """

                    emailext (
                        from: 'Jenkins <lby.ic@libertya.org>',
                        subject: "${emoji} Build ${status} - Libertya Core (dev)",
                        body: mensaje,
                        mimeType: 'text/html',
                        to: 'julian.viejo@disytel.net', 
                    )
                }
            }
        }
    }
}
