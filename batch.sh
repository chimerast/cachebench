#!/bin/bash

# count, loop, threads

./exec.sh 1 1000 1
./exec.sh 10 1000 1
./exec.sh 100 1000 1
./exec.sh 1000 1000 1
./exec.sh 10000 1000 1

./exec.sh 1000 1000 10
./exec.sh 1000 1000 20
./exec.sh 1000 1000 50
./exec.sh 1000 1000 100
