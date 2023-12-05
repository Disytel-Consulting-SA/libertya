#!/bin/bash

# Directorio que contiene los archivos JAR
directory="${1%/}"

# Array para almacenar las clases y sus correspondientes archivos JAR
declare -A classMap

# cantidad de duplicados
duplicados=0

# Función para imprimir clases duplicadas
printDuplicates() {
    for class in "${!classMap[@]}"; do
        files=(${classMap[$class]})
        if [ ${#files[@]} -gt 1 ]; then
            echo "Clase duplicada: $class en ${files[@]}"
	    ((duplicados = duplicados + 1))
        fi
    done
}

echo "Validando existencia de clases duplicadas en $directory"

# Verificar si se proporcionó un directorio
if [ -z "$directory" ]; then
    echo "Uso: $0 <directorio>"
    exit 1
fi

# Verificar si el directorio existe
if [ ! -d "$directory" ]; then
    echo "El directorio no existe: $directory"
    exit 1
fi

# Verificar si el directorio está vacío
if [ -z "$(ls -A "$directory")" ]; then
    echo "Sin archivos para procesar."
    exit 0
fi

# Iterar sobre los archivos JAR en el directorio
for jarFile in "$directory"/*.jar; do
    # Extraer la lista de clases del archivo JAR
    echo "Procesando: $jarFile"
    classes=$(jar tf "$jarFile" | grep -E '\.class$' | sed 's/\.class$//' | tr '/' '.')

    # Iterar sobre las clases y agregarlas al array
    for class in $classes; do
        if [ -n "${classMap[$class]}" ]; then
            classMap["$class"]+=" $jarFile"
        else
            classMap["$class"]="$jarFile"
        fi
    done
done

# Imprimir clases duplicadas
printDuplicates

# Verificar si se encontraron clases duplicadas
if [ "$duplicados" -eq 0 ]; then
    echo "No se encontraron clases duplicadas."
    exit 0
else
	echo "Se encontraron $duplicados clases duplicadas."
    exit 1
fi
