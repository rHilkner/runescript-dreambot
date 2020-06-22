#!/usr/bin/env bash
cd ~/DreamBot/BotData
java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar client.jar