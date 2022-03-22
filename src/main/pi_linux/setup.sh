#!/bin/bash
echo "Setting up code!\n"

echo "Installing python packages to su!"
sudo python -m pip install pynetworktables
sudo python -m pip install opencv-python

echo "Setting up service!"
sudo systemctl daemon-reload
sudo systemctl enable /home/pi/Desktop/rapidreact/src/main/pi_linux/frc.service

echo "Done! Yay!"