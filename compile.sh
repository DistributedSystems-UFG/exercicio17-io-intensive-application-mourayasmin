#!/bin/bash

mkdir -p bin
javac -d bin src/main/java/service/*.java src/main/java/client/*.java

echo "Compilation complete. Classes are in bin/"
