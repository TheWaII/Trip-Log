#!/bin/bash

# Define the list of dangling blob hashes
blobs=("0c46621d1e9ce13c7da8f7a423e090551cf85ece"
       "0c0c3383890637b4721df1f49d0b229e55c0f361"
       "0e3b0f2377a2ba839f6c8d99bd5296dec5d6ed64"
       "14aee1dbc70cea1b36ab9d96b3dd6415f951b14f"
       "1f8b0d9cfe70e77b655fe755272ea9677f378f0a"
       "22cdceb80eae34be589632c1fc9e8a636f814835"
       "2aeb9c5fd0d1f6b081f4919634dba6f7b7a57134"
       "325c444476cd2bd8985a271f6bf9acd0d6294362"
       "3416c3f5cf3f008ed2d72a08ffe827dae7cdb79a"
       "35eb1ddfbbc029bcab630581847471d7f238ec53"
       "3ef89983fcc5fbfc0af58c1a11e1d2b5f228e6c7"
       "3f6e6b31f0dbd5789cbb57b5252aa1f7fe73c340"
       "b1077fbd069ee200af3f5b43415573cb11824334"
       "ceadc15662a45c79be1eb23c0b2182910a432461"
       "cdc89f25a45f5b45f87938d438ba2c302f7499d8"
       "f8051a6f973e69a86e6f07f1a1c87f17a31c7235"
       "fe187c0cf4725efa30e6d8b497b724866b66c73e")

# Loop through each blob and execute git show, saving the output to a file
for blob in "${blobs[@]}"; do
    git show "$blob" >> "$HOME/Desktop/blob_$blob.txt"
done

echo "Files created on the Desktop."

