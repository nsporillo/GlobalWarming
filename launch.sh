#!/bin/sh
#--------------------------------------------------------------------------------
# BUILD GLOBALWARMING (UBUNTU)
#
# SYNOPSIS
#    . launch.sh [CLEAN]
#
# DESCRIPTION
#    Build and deploy the GlobalWarming plugin
#
#    CLEAN
#        Rebuilds the database and clears the plugin config without deleting the
#        world
#
# NOTES
#    This script expects the working directory to be the GlobalWarming directory.
#    Consider running setup.sh first to install the required dependencies and
#    then run this file with the CLEAN option to reset the database, etc. (if you
#    are using a different branch).
#--------------------------------------------------------------------------------

LPAREN="\e[0m[\e[1;94m"
RPAREN="\e[21;0m]"
echo -e "${LPAREN}BUILDING${RPAREN} (CTRL+C TO EXIT)" &&
mvn clean compile install &&
if [[ $1 == CLEAN ]]; then
echo -e "${LPAREN}DELETING THE PLUGIN CONFIGURATION${RPAREN}" &&
sudo rm -rf /home/minecraft/server/plugins/GlobalWarming/* &&
echo -e "${LPAREN}DROPPING THE DATABASE AND USER${RPAREN}" &&
sudo mysql -u root -Bse "DROP DATABASE IF EXISTS GlobalWarming;DROP USER IF EXISTS 'user'@'localhost';" &&
echo -e "${LPAREN}CREATING THE DATABASE, USER AND PERMISSIONS${RPAREN}" &&
sudo mysql -u root -Bse "CREATE DATABASE GlobalWarming;USE GlobalWarming;CREATE USER 'user'@'localhost' IDENTIFIED BY 'pass'; GRANT ALL PRIVILEGES ON GlobalWarming.* TO 'user'@'localhost';"
fi &&
echo -e "${LPAREN}COPYING THE GLOBALWARMING PLUGIN${RPAREN}" &&
sudo cp target/GlobalWarming.jar /home/minecraft/server/plugins/ &&
echo -e "${LPAREN}COPYING THE DATAPACK (CUSTOM ADVANCEMENTS)${RPAREN}" &&
sudo su minecraft -c "cp -r gw_datapack /home/minecraft/server/world/datapacks/" &&
echo -e "${LPAREN}LAUNCHING THE SERVER${RPAREN}" &&
sudo su minecraft -c "/home/minecraft/server/launch.sh"