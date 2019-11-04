#!/bin/bash
java -jar ba-solver-1.0.jar -f $PWD/test_setup/test_formula -g $PWD/test_setup/test_graphs -ke -be -bo -heu -t 2
echo "------------"