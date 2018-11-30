#!/bin/sh
# 1. PURPOSE
#  - Create a Minecraft server with Global Warming plugin on Ubuntu 18 x64 (VM optional)
#  - Includes:
#    - Spigot / Bukkit / CraftBukkit
#    - Latest GlobalWarming plugin
#    - MySQL database and user
#    - OPTIONAL: adds Vault and Essentials (an economy is required by the bounty system)
#  - Safe to re-run (if required)
#  - Requires Internet access
#
# 2. VIRTUALBOX SETUP 5.2.22 (OPTIONAL, PART 1)
#  - Download / install VirtualBox: https://download.virtualbox.org/virtualbox/5.2.22/virtualbox-5.2_5.2.22-126460~Ubuntu~bionic_amd64.deb
#  - Download an Ubuntu iso: http://releases.ubuntu.com/18.04.1/ubuntu-18.04.1-desktop-amd64.iso
#  - Open the VirtualBox Manager
#  - Create a new VM: Linux, Ubuntu 64-bit
#  - Note: May need to enable "Intel Virtualization Technology" in BIOS (i.e., F2 at startup) for x64 support
#  - [VM] > Start > Select the Ubuntu ISO file
#  - Choose the "Minimal OS" installation
#  - Restart once complete
#  - Log into Ubuntu
#  - Open a terminal
#  $ sudo apt-get install virtualbox-guest-additions-iso
#  - VM Menu > Devices > Insert Guest Additions CD Image
#  - Accept the install and wait until complete
#  - Ubuntu: Power off
#  - Close the VirtualBox Manager
#
# 3. VIRUTALBOX SETUP 5.2.22 (OPTIONAL, PART 2)
#  - Re-open the VirtualBox Manager
#  - VirtualBox > Global Tools > Host Network Manager > Create (e.g., vboxnet0)
#  - VirtualBox > Machine Tools
#  - VirtualBox > [VM] > Settings > Advanced > Shared Clipboard / Drag'n'Drop > Bidirectional (x2)
#  - VirtualBox > [VM] > Settings > Network > Adapter #2 (second tab)
#    - Enable
#    - Attached To > Host-Only
#    - Name > vboxnet0 (for example)
#    - OK
#  - VirtualBox > [VM] > Start
#
# 4. GLOBALWARMING PLUGIN SETUP
#  - Copy this file (setup.sh) into /home/[YOUR ACCOUNT]
#  - Open a terminal
#  $ cd ~
#  $ . setup.sh
#  - Accept the Minecraft EULA
#  - Enter your Ubuntu account's password (for sudo)
#  - The following update may take a minute
#  - Accept the Java EULA (enter + left + enter)
#  - "minecraft" server account:
#   - Enter and confirm its password
#   - Accept the default name, etc. (hit enter x6)
#
# 5. SERVER ADDRESS (LOCAL NETWORK)
#  - Ubuntu > open a separate terminal (or new tab)
#  $ ip addr show
#  - Note the new "local" IP address: 192.168.[###].[###]
#
# 6. MINECRAFT CLIENT (LOCAL NETWORK)
#  - Open the Minecraft Launcher
#  - Launch options > Add new > Version "1.13" (specifically)
#  - News > Play
#  - Play Multiplayer > Add Server > 192.168.[###].[###]:[PORT]
#  * Note: the Minecraft server log (in the Ubuntu terminal) will have the port number to use
#  - Connect!
#  - Type "/gw score" in the Mincraft client chat-box to confirm
#
# 7. ESSENTIALS (ECONOMY)
#  -For a minimal "Essentials" plugin install (OPTIONAL):
#  - Edit its config file:
#  $ vi /home/minecraft/server/plugins/config.yml
#  - Under "disabled-commands", replace what's there with the "player-commands" section
#  - Comment out "balance" and "pay" with a "#"
#  - Comment out the "socialspy-commands" section
#  - Comment out the "player-commands" section, leave "balance" and "pay" uncommented
#  - Set the "starting-balance" value to 1000 and "min-money" to 0 (for example)
#  - Ignore the other sections (they were not installed)
#  - Delete all the text in this file:
#  $ vi /home/minecraft/server/plugins/motd.txt
#  - From the server command line:
#  > reload
#
# 8. ADVANCEMENTS
#  - To erase existing advancement progress:
#   - Disconnect players
#   - Delete from /home/minecraft/server/world/advancements/
#   - Reload server (from the server command line, type: reload)
#   - Reconnect players
#
# 9. SERVER RESTART / COMMANDS / STOP
#  $ sudo su minecraft -c "/home/minecraft/server/launch.sh"
#  > give [PLAYER_NAME] stick 2 -> gives a player 2 sticks
#  > reload -> reloads the server
#  > stop -> stops the server
#
# 10. TESTED ON NOV 2018:
#  - VirtualBox 5.2.22 VM
#  - Ubuntu 18.04.1 x64

read -p "By running this script you agree to the Minecraft EULA (https://account.mojang.com/documents/minecraft_eula)"

# System update
sudo apt-get -y update && sudo apt-get -y upgrade

# Install git
sudo apt-get -y install git

# Java (need 1.8)
sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get -y update
sudo apt-get -y install oracle-java8-installer

# Minecraft user
sudo adduser minecraft

# Build Spigot
sudo su minecraft -c "mkdir /home/minecraft/build"
cd /home/minecraft/build
sudo su minecraft -c "rm BuildTools.jar"
sudo su minecraft -c "wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"
sudo su minecraft -c "java -jar BuildTools.jar --rev 1.13"

# Create a server directory
sudo su minecraft -c "mkdir ../server"

# Move Spigot there
cd ../server
sudo su minecraft -c "mv ../build/spigot-1.*.jar spigot.jar"

# Startup script
sudo su minecraft -c 'echo "cd /home/minecraft/server;java -Xms512M -Xmx1536M -jar spigot.jar" >> /home/minecraft/server/launch.sh'

# Make it executable
sudo su minecraft -c "chmod +x /home/minecraft/server/launch.sh"

# Initialize the server
sudo su minecraft -c "java -Xms512M -Xmx1536M -jar spigot.jar"
sudo su minecraft -c "echo 'eula=true' > eula.txt"

# Install Maven
sudo apt-get -y install maven

# Clone the project
cd ~
mkdir build
cd build
git clone https://github.com/nsporillo/GlobalWarming.git

#Build
cd GlobalWarming
mvn clean compile install

# Copy the plugin
cd ~/build/GlobalWarming
sudo su minecraft -c "mkdir /home/minecraft/server/plugins"
sudo su minecraft -c "cp target/GlobalWarming.jar /home/minecraft/server/plugins/"
sudo su minecraft -c "mkdir -p /home/minecraft/server/world/datapacks"
sudo su minecraft -c "cp -r gw_datapack /home/minecraft/server/world/datapacks/"

# Delete any old configuration files (if required)
sudo su minecraft -c "rm /home/minecraft/server/plugins/GlobalWarming/lang.yml"
sudo su minecraft -c "rm /home/minecraft/server/plugins/GlobalWarming/config.yml"
sudo su minecraft -c "rm /home/minecraft/server/plugins/GlobalWarming/plugin.yml"
sudo su minecraft -c "rm /home/minecraft/server/plugins/GlobalWarming/world.yml"

# Get Vault.jar (OPTIONAL)
#  - NOTE: an economy is required by the bounty system
sudo su minecraft -c "wget https://media.forgecdn.net/files/2615/750/Vault.jar -P /home/minecraft/server/plugins/"

# Get Essentials.jar (OPTIONAL)
#  - NOTE: an economy is required by the bounty system
sudo su minecraft -c "wget https://hub.spigotmc.org/jenkins/job/spigot-essentials/lastSuccessfulBuild/artifact/Essentials/target/Essentials-2.x-SNAPSHOT.jar -O /home/minecraft/server/plugins/Essentials.jar"

# Install MySQL server and client
sudo apt-get -y install mysql-server-5.7
sudo apt-get -y install mysql-client-5.7

# Delete the old database (if it exists)
sudo mysql -u root -Bse "DROP DATABASE IF EXISTS GlobalWarming;DROP USER IF EXISTS 'user'@'localhost';"

# Create the database
sudo mysql -u root -Bse "CREATE DATABASE GlobalWarming;USE GlobalWarming;CREATE USER 'user'@'localhost' IDENTIFIED BY 'pass'; GRANT ALL PRIVILEGES ON GlobalWarming.* TO 'user'@'localhost';"

# Start the server
cd ~
sudo su minecraft -c "/home/minecraft/server/launch.sh"