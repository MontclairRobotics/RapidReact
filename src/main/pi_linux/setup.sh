echo "Setting up code!"
sudo python -m pip install pynetworktables
sudo python -m pip install opencv-python
sudo systemctl enable frc.service
sudo systemtl daemon-reload
echo "Done! Yay!"