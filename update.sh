#!/bin/bash
./clean.sh
git reset --hard
git pull
./build.sh