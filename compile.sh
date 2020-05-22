#!/bin/bash

set -e
rm -rf build/ &> /dev/null
mkdir build &> /dev/null
javac -d build/ $(find . -name "*.java") &> /dev/null